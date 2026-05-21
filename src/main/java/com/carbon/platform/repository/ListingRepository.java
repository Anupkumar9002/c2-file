package com.carbon.platform.repository;

import com.carbon.platform.entity.Listing;
import com.carbon.platform.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByFarmerId(Long farmerId);
    List<Listing> findByFarmerUserEmail(String email);
    List<Listing> findByStatus(ListingStatus status);
}
