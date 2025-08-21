package com.innov8ors.insurance.repository.dao;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.repository.NotificationRepository;

import java.util.Optional;

public interface NotificationDao extends NotificationRepository {
    default Optional<Notification> getByIdAndUserId(Long id, Long userId) {
        return findByIdAndUserId(id, userId);
    }
}
