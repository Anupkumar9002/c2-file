package com.carbon.platform.repository;

import com.carbon.platform.entity.Farmer;
import com.carbon.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    Optional<Farmer> findByUserEmail(String email);
    Optional<Farmer> findByUser(User user);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    Optional<Farmer> findByFarmerId(String farmerId);
}
