package com.carbon.platform.repository;

import com.carbon.platform.entity.CreditFormula;
import com.carbon.platform.enums.PracticeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CreditFormulaRepository extends JpaRepository<CreditFormula, Long> {
    Optional<CreditFormula> findByPracticeType(PracticeType practiceType);
    Optional<CreditFormula> findByPracticeTypeAndActiveTrue(PracticeType practiceType);
}
