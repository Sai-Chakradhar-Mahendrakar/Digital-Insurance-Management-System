package com.innov8ors.insurance.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyRenewalRequest {

    @NotNull(message = "Policy ID is required")
    private Long policyId;

    private Boolean acceptUpdatedPremium = true; // Default to true to accept any premium rate changes
}
