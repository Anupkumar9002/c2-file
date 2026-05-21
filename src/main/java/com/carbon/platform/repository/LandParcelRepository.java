package com.carbon.platform.repository;

import com.carbon.platform.entity.LandParcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LandParcelRepository extends JpaRepository<LandParcel, Long> {
    List<LandParcel> findByFarmerId(Long farmerId);
    List<LandParcel> findByFarmerUserEmail(String email);
}
