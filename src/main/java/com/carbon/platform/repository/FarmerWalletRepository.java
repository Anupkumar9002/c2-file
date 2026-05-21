package com.carbon.platform.repository;

import com.carbon.platform.entity.FarmerWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FarmerWalletRepository extends JpaRepository<FarmerWallet, Long> {
    Optional<FarmerWallet> findByFarmerId(Long farmerId);
    Optional<FarmerWallet> findByFarmerUserEmail(String email);
}
