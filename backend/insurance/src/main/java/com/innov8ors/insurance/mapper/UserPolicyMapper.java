package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;

public class UserPolicyMapper {

    public static UserPolicy getUserPolicyFromRequest(Long userId, Long policyId,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      PolicyStatus status, BigDecimal premiumPaid) {
        return new UserPolicy(userId, policyId, startDate, endDate, status, premiumPaid);
    }

    public static UserPolicyResponse getResponseFromUserPolicy(UserPolicy userPolicy) {
        return MAPPER.map(userPolicy, UserPolicyResponse.class);
    }
}