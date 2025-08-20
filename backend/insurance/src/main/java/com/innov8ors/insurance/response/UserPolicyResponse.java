package com.innov8ors.insurance.response;

import com.innov8ors.insurance.enums.UserPolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPolicyResponse {
    private Long id;
    private Long policyId;
    private String policyName;
    private String policyType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UserPolicyStatus status;
    private BigDecimal premiumPaid;

    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAddress;
}