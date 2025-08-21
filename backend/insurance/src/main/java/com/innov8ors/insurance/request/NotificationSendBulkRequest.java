package com.innov8ors.insurance.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationSendBulkRequest {
    @NotNull
    private List<Long> userId;

    @Valid
    @NotNull
    private NotificationSendRequest notificationSendRequest;
}
