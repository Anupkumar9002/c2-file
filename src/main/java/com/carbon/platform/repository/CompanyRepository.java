package com.carbon.platform.repository;

import com.carbon.platform.entity.Company;
import com.carbon.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserEmail(String email);
    Optional<Company> findByUser(User user);
    boolean existsByGstNumber(String gstNumber);
    boolean existsByPanNumber(String panNumber);
}
