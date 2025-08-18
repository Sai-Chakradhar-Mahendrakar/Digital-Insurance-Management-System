package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsForUser(Long userId);
    void sendNotification(NotificationSendRequest request);
    void markAsRead(Long notificationId);
}

