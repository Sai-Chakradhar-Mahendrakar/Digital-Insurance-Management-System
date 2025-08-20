package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
}
