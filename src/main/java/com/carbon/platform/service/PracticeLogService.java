package com.carbon.platform.service;

import com.carbon.platform.entity.Farmer;
import com.carbon.platform.entity.LandParcel;
import com.carbon.platform.entity.PracticeLog;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.enums.CropCategory;
import com.carbon.platform.enums.GrowingSeason;
import com.carbon.platform.enums.PracticeStatus;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.FarmerRepository;
import com.carbon.platform.repository.LandParcelRepository;
import com.carbon.platform.repository.PracticeLogRepository;
import com.carbon.platform.repository.CarbonCreditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class PracticeLogService {

    private final PracticeLogRepository practiceLogRepository;
    private final FarmerRepository farmerRepository;
    private final LandParcelRepository landParcelRepository;
    private final CarbonCreditRepository carbonCreditRepository;

    public PracticeLogService(PracticeLogRepository practiceLogRepository,
                              FarmerRepository farmerRepository,
                              LandParcelRepository landParcelRepository,
                              CarbonCreditRepository carbonCreditRepository) {
        this.practiceLogRepository = practiceLogRepository;
        this.farmerRepository = farmerRepository;
        this.landParcelRepository = landParcelRepository;
        this.carbonCreditRepository = carbonCreditRepository;
    }

    @Transactional
    public PracticeLog logPractice(String farmerEmail, Long parcelId, PracticeType practiceType,
                                   CropCategory cropCategory, GrowingSeason growingSeason,
                                   Double quantity, LocalDate sowingDate, LocalDate harvestingDate,
                                   String proofDocument) {

        Farmer farmer = farmerRepository.findByUserEmail(farmerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Farmer profile not found for user: " + farmerEmail));

        if (farmer.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new InvalidActionException("Only APPROVED farmers can log practices.");
        }

        LandParcel parcel = landParcelRepository.findById(parcelId)
                .orElseThrow(() -> new EntityNotFoundException("Land parcel not found with ID: " + parcelId));

        if (!parcel.getFarmer().getId().equals(farmer.getId())) {
            throw new InvalidActionException("This land parcel does not belong to you.");
        }

        if (quantity <= 0) {
            throw new InvalidActionException("Logged quantity must be positive and non-zero.");
        }

        if (sowingDate != null && harvestingDate != null && sowingDate.isAfter(harvestingDate)) {
            throw new InvalidActionException("Sowing date must be before harvesting date.");
        }

        PracticeLog log = new PracticeLog();
        log.setFarmer(farmer);
        log.setParcel(parcel);
        log.setPracticeType(practiceType);
        log.setCropCategory(cropCategory);
        log.setGrowingSeason(growingSeason);
        log.setQuantity(quantity);
        log.setSowingDate(sowingDate);
        log.setHarvestingDate(harvestingDate);
        log.setProofDocument(proofDocument);
        log.setStatus(PracticeStatus.PENDING);
        log.setSubmissionCount(1);

        return practiceLogRepository.save(log);
    }

    @Transactional
    public PracticeLog resubmitPractice(String farmerEmail, Long logId, PracticeType practiceType,
                                        CropCategory cropCategory, GrowingSeason growingSeason,
                                        Double quantity, LocalDate sowingDate, LocalDate harvestingDate,
                                        String proofDocument) {

        PracticeLog log = practiceLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Practice log not found with ID: " + logId));

        if (!log.getFarmer().getUser().getEmail().equals(farmerEmail)) {
            throw new InvalidActionException("You are not authorized to resubmit this practice log.");
        }

        if (log.getStatus() != PracticeStatus.REJECTED) {
            throw new InvalidActionException("Only REJECTED logs can be resubmitted.");
        }

        if (quantity != null && quantity <= 0) {
            throw new InvalidActionException("Quantity must be positive and non-zero.");
        }

        LocalDate newSowingDate = sowingDate != null ? sowingDate : log.getSowingDate();
        LocalDate newHarvestingDate = harvestingDate != null ? harvestingDate : log.getHarvestingDate();
        if (newSowingDate != null && newHarvestingDate != null && newSowingDate.isAfter(newHarvestingDate)) {
            throw new InvalidActionException("Sowing date must be before harvesting date.");
        }

        // Apply changes
        if (practiceType != null) log.setPracticeType(practiceType);
        if (cropCategory != null) log.setCropCategory(cropCategory);
        if (growingSeason != null) log.setGrowingSeason(growingSeason);
        if (quantity != null) log.setQuantity(quantity);
        if (sowingDate != null) log.setSowingDate(sowingDate);
        if (harvestingDate != null) log.setHarvestingDate(harvestingDate);
        if (proofDocument != null) log.setProofDocument(proofDocument);

        log.setStatus(PracticeStatus.PENDING);
        log.setSubmissionCount(log.getSubmissionCount() + 1);

        return practiceLogRepository.save(log);
    }

    public List<PracticeLog> getLogsByFarmer(String farmerEmail) {
        List<PracticeLog> logs = practiceLogRepository.findByFarmerUserEmail(farmerEmail);
        for (PracticeLog log : logs) {
            carbonCreditRepository.findByPracticeLogId(log.getId())
                    .ifPresent(credit -> log.setCalculatedCredits(credit.getFinalCredits()));
        }
        return logs;
    }

    public List<PracticeLog> getAllLogs() {
        List<PracticeLog> logs = practiceLogRepository.findAll();
        for (PracticeLog log : logs) {
            carbonCreditRepository.findByPracticeLogId(log.getId())
                    .ifPresent(credit -> log.setCalculatedCredits(credit.getFinalCredits()));
        }
        return logs;
    }
}
