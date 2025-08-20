package com.innov8ors.insurance.response;

import com.innov8ors.insurance.enums.ClaimStatus;
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
public class ClaimResponse {
    private Long id;
    private Long userPolicyId;
    private LocalDate claimDate;
    private BigDecimal claimAmount;
    private String reason;
    private ClaimStatus status;
    private String reviewerComment;
    private LocalDate resolvedDate;
    private String policyName;
    private String policyType;
}
