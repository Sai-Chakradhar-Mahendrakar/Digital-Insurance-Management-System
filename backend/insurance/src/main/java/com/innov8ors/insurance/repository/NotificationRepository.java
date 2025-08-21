package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends BaseRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
