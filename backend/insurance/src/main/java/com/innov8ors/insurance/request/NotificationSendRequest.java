package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
@Builder
public class NotificationSendRequest {
    @NotNull
    private Long userId;

    @NotNull
    private String message;

    @NotNull
    private NotificationType type = NotificationType.GENERAL;
}
