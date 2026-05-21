package com.carbon.platform.repository;

import com.carbon.platform.entity.CarbonCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarbonCreditRepository extends JpaRepository<CarbonCredit, Long> {
    List<CarbonCredit> findByFarmerId(Long farmerId);
    @Query("SELECT COALESCE(SUM(c.finalCredits),0) FROM CarbonCredit c WHERE c.farmer.user.email = :email")
    Double sumFinalCreditsByFarmerUserEmail(@Param("email") String email);
    Optional<CarbonCredit> findByPracticeLogId(Long practiceLogId);
}
