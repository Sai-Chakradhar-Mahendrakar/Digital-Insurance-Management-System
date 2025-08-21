package com.innov8ors.insurance.util;

import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.mapper.NotificationMapper;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.service.NotificationService;

public class NotificationUtil {
    public static void send(NotificationService notificationService, Long userId, String message, NotificationType type) {
        NotificationSendRequest request = NotificationMapper.createNotificationSendRequest(userId, message, type);
        notificationService.sendNotification(request);
    }
}

