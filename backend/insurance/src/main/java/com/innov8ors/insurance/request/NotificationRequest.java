package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.NotificationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    @NotEmpty
    private String message;

    @NotNull
    private NotificationType type = NotificationType.GENERAL;
}
