package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationPaginatedResponse;
import com.innov8ors.insurance.response.NotificationResponse;
import org.springframework.data.domain.Page;

public class NotificationMapper {
    public static NotificationSendRequest toSupportTicketNotification(Long userId, String message, NotificationType type) {
        return NotificationSendRequest.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .build();
    }

    public static NotificationPaginatedResponse getNotificationPaginatedResponse(Page<Notification> notifications, Integer page, Integer size) {
        return NotificationPaginatedResponse.builder()
                .notifications(notifications.stream()
                        .map(NotificationMapper::toNotificationResponse)
                        .toList())
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .size(size)
                .page(page)
                .build();
    }

    public static NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
