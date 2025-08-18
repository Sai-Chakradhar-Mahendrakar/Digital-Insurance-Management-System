package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.response.PolicyPaginatedResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;

public class PolicyMapper {
    public static PolicyPaginatedResponse getResponseFromPoliciesPage(Page<Policy> policiesPage) {
        return PolicyPaginatedResponse.builder()
                .policies(policiesPage.getContent())
                .totalElements(policiesPage.getTotalElements())
                .totalPages(policiesPage.getTotalPages())
                .page(policiesPage.getPageable().getPageNumber())
                .size(policiesPage.getSize())
                .build();
    }

    public static Policy getPolicyFromRequest(PolicyCreateRequest policyCreateRequest) {
        Policy policy = MAPPER.map(policyCreateRequest, Policy.class);
        policy.setCreatedAt(LocalDateTime.now());
        return policy;
    }
}
