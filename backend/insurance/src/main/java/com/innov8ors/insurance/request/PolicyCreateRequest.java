package com.innov8ors.insurance.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyCreateRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String type;

    private String description;

    @NotNull
    @Positive
    private BigDecimal premiumAmount;

    @NotNull
    @Positive
    private BigDecimal coverageAmount;

    @NotNull
    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationMonths;

    @NotNull
    @Positive
    private BigDecimal renewalPremiumRate;
}