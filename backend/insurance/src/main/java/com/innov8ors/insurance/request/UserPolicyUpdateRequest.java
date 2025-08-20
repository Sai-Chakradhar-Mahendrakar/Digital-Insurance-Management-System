package com.innov8ors.insurance.request;

import com.innov8ors.insurance.enums.UserPolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPolicyUpdateRequest {
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private UserPolicyStatus status;

    private BigDecimal premiumPaid;

    private BigDecimal totalAmountClaimed;
}
