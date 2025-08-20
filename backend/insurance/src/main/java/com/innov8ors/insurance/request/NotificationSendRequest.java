package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationSendRequest {
    private Long userId;
    private String message;
    private NotificationType type;
}

