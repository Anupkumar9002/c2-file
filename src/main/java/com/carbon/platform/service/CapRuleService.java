package com.carbon.platform.service;

import com.carbon.platform.entity.CreditFormula;
import com.carbon.platform.enums.CropCategory;
import com.carbon.platform.enums.GrowingSeason;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.repository.CreditFormulaRepository;
import org.springframework.stereotype.Service;

@Service
public class CapRuleService {

    private final CreditFormulaRepository creditFormulaRepository;

    public CapRuleService(CreditFormulaRepository creditFormulaRepository) {
        this.creditFormulaRepository = creditFormulaRepository;
    }

    public double calculateCap(PracticeType practiceType, double areaInAcres, CropCategory cropCategory, GrowingSeason growingSeason) {
        CreditFormula formula = creditFormulaRepository.findByPracticeTypeAndActiveTrue(practiceType)
                .orElseGet(() -> creditFormulaRepository.findByPracticeType(practiceType)
                        .orElse(null));

        double baseCap = 100.0;
        if (formula != null && formula.getMaxCap() != null) {
            baseCap = formula.getMaxCap();
        }

        // Cap scaled by area (e.g. max cap per acre)
        return baseCap * areaInAcres;
    }
}
