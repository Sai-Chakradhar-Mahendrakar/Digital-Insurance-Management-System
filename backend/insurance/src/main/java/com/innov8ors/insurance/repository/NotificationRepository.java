package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Additional query methods if needed
}

