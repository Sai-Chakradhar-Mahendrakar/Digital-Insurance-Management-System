package com.innov8ors.insurance.response;

import com.innov8ors.insurance.enums.PolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPolicyResponse {
    private Long id;
    private Long policyId;
    private String policyName;
    private String policyType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PolicyStatus status;
    private BigDecimal premiumPaid;
}