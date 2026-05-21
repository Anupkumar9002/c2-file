package com.carbon.platform.service;

import com.carbon.platform.dto.response.CreditCalculationResult;
import com.carbon.platform.entity.CreditFormula;
import com.carbon.platform.entity.LandParcel;
import com.carbon.platform.entity.PracticeLog;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.repository.CreditFormulaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreditCalculationEngine {

    private final CreditFormulaRepository creditFormulaRepository;
    private final CapRuleService capRuleService;
    private final ObjectMapper objectMapper;

    public CreditCalculationEngine(CreditFormulaRepository creditFormulaRepository,
                                   CapRuleService capRuleService,
                                   ObjectMapper objectMapper) {
        this.creditFormulaRepository = creditFormulaRepository;
        this.capRuleService = capRuleService;
        this.objectMapper = objectMapper;
    }

    public CreditCalculationResult calculate(PracticeLog log, LandParcel parcel) {
        // 1. Load active formula for PracticeType
        CreditFormula formula = creditFormulaRepository.findByPracticeTypeAndActiveTrue(log.getPracticeType())
                .orElseGet(() -> creditFormulaRepository.findByPracticeType(log.getPracticeType())
                        .orElseThrow(() -> new EntityNotFoundException("Active credit formula not found for practice: " + log.getPracticeType())));

        // 2. Apply base calculation
        double area = parcel.getAreaInAcres() != null ? parcel.getAreaInAcres() : 0.0;
        double quantity = log.getQuantity() != null ? log.getQuantity() : 0.0;
        double rawCredits = formula.getBaseCoefficient() * area * quantity;

        // 3. Apply seasonal multiplier
        double seasonMultiplier = formula.getSeasonMultiplier(log.getGrowingSeason());
        rawCredits *= seasonMultiplier;

        // 4. Apply crop category multiplier
        double cropMultiplier = formula.getCropCategoryMultiplier(log.getCropCategory());
        rawCredits *= cropMultiplier;

        // 5. Apply duration factor from sowing to harvesting dates
        long days = 0;
        if (log.getSowingDate() != null && log.getHarvestingDate() != null) {
            days = ChronoUnit.DAYS.between(log.getSowingDate(), log.getHarvestingDate());
        }
        double durationFactor = formula.getDurationFactor(days);
        rawCredits *= durationFactor;

        // 6. Apply caps (US-011)
        double cap = capRuleService.calculateCap(log.getPracticeType(), area, log.getCropCategory(), log.getGrowingSeason());
        boolean wasCapped = rawCredits > cap;
        double finalCredits = wasCapped ? cap : rawCredits;

        // Build details JSON
        String detailsJson = "{}";
        try {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("baseCoefficient", formula.getBaseCoefficient());
            detailsMap.put("landAreaAcres", area);
            detailsMap.put("logQuantity", quantity);
            detailsMap.put("season", log.getGrowingSeason() != null ? log.getGrowingSeason().name() : "N/A");
            detailsMap.put("seasonMultiplier", seasonMultiplier);
            detailsMap.put("cropCategory", log.getCropCategory() != null ? log.getCropCategory().name() : "N/A");
            detailsMap.put("cropMultiplier", cropMultiplier);
            detailsMap.put("sowingDate", log.getSowingDate());
            detailsMap.put("harvestingDate", log.getHarvestingDate());
            detailsMap.put("durationDays", days);
            detailsMap.put("durationFactor", durationFactor);
            detailsMap.put("capPerAcreMultiplier", formula.getMaxCap());
            detailsMap.put("calculatedCap", cap);
            detailsMap.put("rawCalculatedCredits", rawCredits);
            detailsMap.put("wasCapped", wasCapped);
            detailsMap.put("finalCredits", finalCredits);
            detailsJson = objectMapper.writeValueAsString(detailsMap);
        } catch (Exception e) {
            System.err.println("Failed to serialize calculation details: " + e.getMessage());
        }

        return new CreditCalculationResult(
                rawCredits,
                finalCredits,
                wasCapped,
                wasCapped ? "Exceeded land cap of " + cap + " credits for practice type " + log.getPracticeType() : null,
                formula.getVersion(),
                detailsJson
        );
    }
}
