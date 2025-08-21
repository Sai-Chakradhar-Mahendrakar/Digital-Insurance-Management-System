package com.innov8ors.insurance.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class PolicyPurchaseRequest {
    @NotNull
    private Long policyId;
    @NotNull
    private BigDecimal premiumPaid;

    public PolicyPurchaseRequest() {}

    public PolicyPurchaseRequest(Long policyId, BigDecimal premiumPaid) {
        this.policyId = policyId;
        this.premiumPaid = premiumPaid;
    }
}