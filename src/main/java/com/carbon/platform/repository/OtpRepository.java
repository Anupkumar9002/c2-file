package com.carbon.platform.repository;

import com.carbon.platform.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findTopByTargetAndCodeAndUsedFalseOrderByExpiryDesc(String target, String code);
    Optional<OtpEntity> findTopByTargetOrderByExpiryDesc(String target);
}
