package com.carbon.platform.service;

import com.carbon.platform.dto.response.CreditCalculationResult;
import com.carbon.platform.entity.*;
import com.carbon.platform.enums.*;
import com.carbon.platform.event.*;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final CompanyRepository companyRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final CarbonCreditRepository carbonCreditRepository;
    private final MarketplaceService marketplaceService;
    private final CreditFormulaRepository creditFormulaRepository;
    private final CreditCalculationEngine calculationEngine;
    private final ApplicationEventPublisher eventPublisher;

    public AdminService(UserRepository userRepository,
                        FarmerRepository farmerRepository,
                        CompanyRepository companyRepository,
                        PracticeLogRepository practiceLogRepository,
                        CarbonCreditRepository carbonCreditRepository,
                        MarketplaceService marketplaceService,
                        CreditFormulaRepository creditFormulaRepository,
                        CreditCalculationEngine calculationEngine,
                        ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.farmerRepository = farmerRepository;
        this.companyRepository = companyRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.carbonCreditRepository = carbonCreditRepository;
        this.marketplaceService = marketplaceService;
        this.creditFormulaRepository = creditFormulaRepository;
        this.calculationEngine = calculationEngine;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void verifyFarmer(Long farmerId, ApprovalStatus status, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin comments are mandatory for both approval and rejection actions.");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new InvalidActionException("Invalid verification status. Must be APPROVED or REJECTED");
        }

        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Farmer profile not found with ID: " + farmerId));

        farmer.setApprovalStatus(status);
        farmer.setAdminComments(comments);

        User user = farmer.getUser();
        user.setStatus(status);

        if (status == ApprovalStatus.APPROVED) {
            farmer.setFarmerId("FARM-" + String.format("%05d", farmer.getId()));
            farmer.setApprovedAt(LocalDateTime.now());
        }

        farmerRepository.save(farmer);
        userRepository.save(user);

        // Publish approval event
        eventPublisher.publishEvent(new FarmerApprovalEvent(user.getId(), user.getEmail(), status.name(), comments));
    }

    @Transactional
    public void verifyCompany(Long companyId, ApprovalStatus status, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin comments are mandatory for both approval and rejection actions.");
        }
        if (status != ApprovalStatus.APPROVED && status != ApprovalStatus.REJECTED) {
            throw new InvalidActionException("Invalid verification status. Must be APPROVED or REJECTED");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company profile not found with ID: " + companyId));

        company.setApprovalStatus(status);
        company.setAdminComments(comments);

        User user = company.getUser();
        user.setStatus(status);

        companyRepository.save(company);
        userRepository.save(user);

        // Publish approval event
        eventPublisher.publishEvent(new FarmerApprovalEvent(user.getId(), user.getEmail(), status.name(), comments));
    }

    @Transactional
    public void verifyPracticeLog(Long logId, PracticeStatus status, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin comments are mandatory for both approval and rejection actions.");
        }
        if (status != PracticeStatus.APPROVED && status != PracticeStatus.REJECTED) {
            throw new InvalidActionException("Invalid verification status. Must be APPROVED or REJECTED");
        }

        PracticeLog log = practiceLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Practice log not found with ID: " + logId));

        log.setStatus(status);
        log.setAdminComments(comments);
        practiceLogRepository.save(log);

        Farmer farmer = log.getFarmer();
        User user = farmer.getUser();

        // Publish Practice Log verification event
        eventPublisher.publishEvent(new PracticeApprovalEvent(user.getId(), log.getParcel().getParcelName(), status.name(), comments));

        if (status == PracticeStatus.APPROVED) {
            // Trigger credit calculation engine
            CreditCalculationResult result = calculationEngine.calculate(log, log.getParcel());

            // Save carbon credit record
            CarbonCredit credit = new CarbonCredit();
            credit.setPracticeLog(log);
            credit.setFarmer(farmer);
            credit.setRawCalculatedCredits(result.getRawCredits());
            credit.setCappedCredits(result.isWasCapped() ? result.getFinalCredits() : result.getRawCredits());
            credit.setFinalCredits(result.getFinalCredits());
            credit.setFormulaVersion(result.getFormulaVersion());
            credit.setCalculationDetails(result.getCalculationDetails());
            credit.setWasCapped(result.isWasCapped());
            credit.setCapReason(result.getCapReason());
            credit.setStatus(CreditStatus.PENDING_VERIFICATION); // Must be verified by admin in a separate step

            carbonCreditRepository.save(credit);
        }
    }

    @Transactional
    public void verifyCarbonCredit(Long creditId, CreditStatus status, String comments) {
        verifyCarbonCredit(creditId, status, comments, null, null);
    }

    @Transactional
    public void verifyCarbonCredit(Long creditId, CreditStatus status, String comments, Double pricePerCredit, Double quantity) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin comments are mandatory for both approval and rejection actions.");
        }
        if (status != CreditStatus.VERIFIED && status != CreditStatus.REJECTED) {
            throw new InvalidActionException("Invalid status. Must be VERIFIED or REJECTED");
        }

        CarbonCredit credit = carbonCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("Carbon credit not found with ID: " + creditId));

        credit.setStatus(status);
        if (status == CreditStatus.REJECTED) {
            credit.setCapReason("Rejected by Admin: " + comments);
        }
        carbonCreditRepository.save(credit);

        if (status == CreditStatus.VERIFIED && pricePerCredit != null && pricePerCredit > 0) {
            // Automatically create a marketplace listing for the verified credit
            try {
                double finalQty = (quantity != null && quantity > 0) ? quantity : credit.getFinalCredits();
                marketplaceService.createListing(
                    credit.getFarmer().getUser().getEmail(),
                    credit.getId(),
                    finalQty,
                    pricePerCredit,
                    null
                );
            } catch (Exception e) {
                // Log and continue; marketplace listing is optional and should not block credit verification
            }
        }
    }

    @Transactional
    public void listVerifiedCredit(Long creditId, Double pricePerCredit, Double quantity, java.time.LocalDate validUntil) {
        marketplaceService.createListingForVerifiedCredit(creditId, pricePerCredit, quantity, validUntil);
    }


    @Transactional
    public void configureFormula(PracticeType type, Double baseCoefficient, Double maxCap, String version) {
        CreditFormula formula = creditFormulaRepository.findByPracticeType(type)
                .orElse(new CreditFormula());

        formula.setPracticeType(type);
        formula.setBaseCoefficient(baseCoefficient);
        formula.setMaxCap(maxCap);
        formula.setVersion(version);
        formula.setActive(true);

        creditFormulaRepository.save(formula);
    }
}
