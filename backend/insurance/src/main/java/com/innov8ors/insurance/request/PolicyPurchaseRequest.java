package com.innov8ors.insurance.request;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class PolicyPurchaseRequest {
    private Long policyId;
    private BigDecimal premiumPaid;

    public PolicyPurchaseRequest() {}

    public PolicyPurchaseRequest(Long policyId, BigDecimal premiumPaid) {
        this.policyId = policyId;
        this.premiumPaid = premiumPaid;
    }
}