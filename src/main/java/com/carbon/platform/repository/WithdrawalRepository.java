package com.carbon.platform.repository;

import com.carbon.platform.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByFarmerId(Long farmerId);
    List<Withdrawal> findByFarmerUserEmail(String email);
}
