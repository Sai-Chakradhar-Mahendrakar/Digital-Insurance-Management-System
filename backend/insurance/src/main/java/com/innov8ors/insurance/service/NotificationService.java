package com.innov8ors.insurance.service;

import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.request.NotificationSendBulkRequest;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationPaginatedResponse;
import com.innov8ors.insurance.response.NotificationResponse;

public interface NotificationService {

    NotificationPaginatedResponse getNotificationsByUserId(Long userId, NotificationStatus notificationStatus, NotificationType notificationType, Integer page, Integer size);

    void sendNotificationsBulk(NotificationSendBulkRequest request);

    NotificationResponse sendNotification(NotificationSendRequest request);

    void markNotificationAsRead(Long notificationId, Long userId);

    void markAllNotificationsAsRead(Long userId);
}
