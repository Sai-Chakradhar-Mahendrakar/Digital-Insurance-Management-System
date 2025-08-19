package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.enums.UserPolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;

public class UserPolicyMapper {
    public static UserPolicyResponse getResponseFromUserPolicy(UserPolicy userPolicy) {
        return MAPPER.map(userPolicy, UserPolicyResponse.class);
    }

    public static UserPolicyResponse convertToResponse(UserPolicy userPolicy) {
        return UserPolicyResponse.builder()
                .id(userPolicy.getId())
                .policyId(userPolicy.getPolicyId())
                .policyName(userPolicy.getPolicy().getName())
                .policyType(userPolicy.getPolicy().getType())
                .startDate(userPolicy.getStartDate())
                .endDate(userPolicy.getEndDate())
                .status(userPolicy.getStatus())
                .premiumPaid(userPolicy.getPremiumPaid())
                .build();
    }
}