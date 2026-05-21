package com.carbon.platform.repository;

import com.carbon.platform.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateId(String certificateId);
    Optional<Certificate> findByTransactionId(Long transactionId);
    Optional<Certificate> findByTransactionTransactionId(String transactionId);
    List<Certificate> findByTransactionCompanyUserEmail(String email);
}
