package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;

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
                .totalAmountClaimed(userPolicy.getTotalAmountClaimed())
                .build();
    }

    public static UserPolicyPaginatedResponse getPolicyPaginatedResponse(Page<UserPolicy> userPolicies, Integer page, Integer size) {
        return UserPolicyPaginatedResponse.builder()
                .userPolicies(userPolicies.getContent())
                .page(page)
                .size(size)
                .totalElements(userPolicies.getTotalElements())
                .totalPages(userPolicies.getTotalPages())
                .build();
    }
}