package com.innov8ors.insurance.response;

import com.innov8ors.insurance.enums.NotificationStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
