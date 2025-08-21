package com.innov8ors.insurance.response;

import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.enums.NotificationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

