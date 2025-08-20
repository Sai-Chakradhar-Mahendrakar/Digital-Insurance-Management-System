package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getNotificationsByUserId(Long userId);

    NotificationResponse sendNotification(NotificationSendRequest request);

    void markNotificationAsRead(Long notificationId, Long userId);

    void markAllNotificationsAsRead(Long userId);
}
