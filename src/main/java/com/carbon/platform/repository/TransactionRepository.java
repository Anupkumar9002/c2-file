package com.carbon.platform.repository;

import com.carbon.platform.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompanyId(Long companyId);
    List<Transaction> findByCompanyUserEmail(String email);
    List<Transaction> findByListingFarmerId(Long farmerId);
    List<Transaction> findByListingFarmerUserEmail(String email);
    Optional<Transaction> findByTransactionId(String transactionId);
}
