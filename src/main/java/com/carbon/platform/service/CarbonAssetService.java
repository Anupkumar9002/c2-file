package com.carbon.platform.service;

import com.carbon.platform.dto.CarbonAssetDto;
import com.carbon.platform.entity.CarbonCredit;
import com.carbon.platform.entity.Farmer;
import com.carbon.platform.repository.CarbonCreditRepository;
import com.carbon.platform.repository.FarmerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarbonAssetService {

    private final CarbonCreditRepository carbonCreditRepository;
    private final FarmerRepository farmerRepository;

    public CarbonAssetService(CarbonCreditRepository carbonCreditRepository, FarmerRepository farmerRepository) {
        this.carbonCreditRepository = carbonCreditRepository;
        this.farmerRepository = farmerRepository;
    }

    public List<CarbonAssetDto> getCarbonAssets(String email) {
        Farmer farmer = farmerRepository.findByUserEmail(email).orElse(null);
        if (farmer == null) {
            return List.of();
        }
        List<CarbonCredit> credits = carbonCreditRepository.findByFarmerId(farmer.getId());
        return credits.stream()
                .filter(credit -> credit.getPracticeLog() != null)
                .map(credit -> {
                    String capApplied = credit.isWasCapped() ? "Capped" : "Uncapped";
                    String status = credit.getStatus() != null ? credit.getStatus().name() : "PENDING_VERIFICATION";
                    return new CarbonAssetDto(
                            credit.getPracticeLog().getId(),
                            credit.getFinalCredits(),
                            credit.getFormulaVersion(),
                            capApplied,
                            credit.getCalculatedAt(),
                            status,
                            credit.getCalculationDetails()
                    );
                }).collect(Collectors.toList());
    }
}
