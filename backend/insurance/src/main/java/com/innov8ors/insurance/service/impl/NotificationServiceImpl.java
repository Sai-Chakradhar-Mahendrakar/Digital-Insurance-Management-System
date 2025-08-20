package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.repository.NotificationRepository;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            throw new IllegalArgumentException("Notification not found");
        }
        Notification notification = optionalNotification.get();
        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to mark this notification as read");
        }
        notification.setStatus(com.innov8ors.insurance.enums.NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public java.util.List<NotificationResponse> getNotificationsByUserId(Long userId) {
        java.util.List<Notification> notifications = notificationRepository.findAll().stream()
            .filter(n -> n.getUser().getId().equals(userId))
            .collect(Collectors.toList());
        return notifications.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public NotificationResponse sendNotification(NotificationSendRequest request) {
        Notification notification = new Notification();
        User user = new User();
        user.setId(request.getUserId());
        notification.setUser(user);
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(java.time.LocalDateTime.now());
        notification.setReadAt(null);
        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    @Override
    public void markAllNotificationsAsRead(Long userId) {
        java.util.List<Notification> notifications = notificationRepository.findAll().stream()
            .filter(n -> n.getUser().getId().equals(userId) && n.getStatus() == NotificationStatus.UNREAD)
            .collect(Collectors.toList());
        for (Notification notification : notifications) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(java.time.LocalDateTime.now());
        }
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUser().getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setStatus(notification.getStatus());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }
}
