package com.innov8ors.insurance.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimCreateRequest {
    @NotNull(message = "User policy ID is required")
    private Long policyId;

    @NotNull(message = "Claim date is required")
    private LocalDate claimDate;

    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    private BigDecimal claimAmount;

    @NotEmpty(message = "Reason is required")
    private String reason;
}
