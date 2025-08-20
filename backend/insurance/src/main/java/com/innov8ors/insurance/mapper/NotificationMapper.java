package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.request.NotificationSendRequest;

public class NotificationMapper {
    public static NotificationSendRequest toSupportTicketNotification(Long userId, String message, NotificationType type) {
        NotificationSendRequest notificationRequest = new NotificationSendRequest();
        notificationRequest.setUserId(userId);
        notificationRequest.setMessage(message);
        notificationRequest.setType(type);
        return notificationRequest;
    }
}
