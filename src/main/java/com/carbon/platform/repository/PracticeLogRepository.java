package com.carbon.platform.repository;

import com.carbon.platform.entity.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {
    List<PracticeLog> findByFarmerId(Long farmerId);
    List<PracticeLog> findByFarmerUserEmail(String email);
}
