package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;

public class UserPolicyMapper {

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
                .userId(userPolicy.getUser().getId())
                .userName(userPolicy.getUser().getName())
                .userEmail(userPolicy.getUser().getEmail())
                .userPhone(userPolicy.getUser().getPhone())
                .userAddress(userPolicy.getUser().getAddress())
                .coverageAmount(userPolicy.getPolicy().getCoverageAmount())
                .build();
    }

    public static UserPolicyPaginatedResponse getPolicyPaginatedResponse(Page<UserPolicy> userPolicies, Integer page, Integer size) {
        return UserPolicyPaginatedResponse.builder()
                .userPolicies(userPolicies.getContent()
                        .stream().map(UserPolicyMapper::convertToResponse)
                        .toList())
                .page(page)
                .size(size)
                .totalElements(userPolicies.getTotalElements())
                .totalPages(userPolicies.getTotalPages())
                .build();
    }
}