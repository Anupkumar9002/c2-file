package com.carbon.platform.repository;

import com.carbon.platform.entity.InAppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Long> {
    List<InAppNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<InAppNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
}
