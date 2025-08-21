package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.NotificationType;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class NotificationSendBulkRequest {
    @NotNull
    private List<Long> userId;

    @NotNull
    private String message;

    @NotNull
    private NotificationType type = NotificationType.GENERAL;
}
