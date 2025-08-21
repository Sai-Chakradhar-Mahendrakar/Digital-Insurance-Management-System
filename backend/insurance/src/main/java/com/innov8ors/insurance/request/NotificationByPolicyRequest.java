package com.innov8ors.insurance.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationByPolicyRequest {
    @NotNull
    private Long policyId;

    @NotNull
    @Valid
    private NotificationRequest request;
}
