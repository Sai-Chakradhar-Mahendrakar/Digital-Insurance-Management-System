package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.response.PolicyPaginatedResponse;
import org.springframework.data.domain.Page;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;

public class PolicyMapper {
    public static PolicyPaginatedResponse getResponseFromPoliciesPage(Page<Policy> policiesPage, Integer page, Integer size) {
        return PolicyPaginatedResponse.builder()
                .policies(policiesPage.getContent())
                .totalElements(policiesPage.getTotalElements())
                .totalPages(policiesPage.getTotalPages())
                .page(page)
                .size(size)
                .build();
    }

    public static Policy getPolicyFromRequest(PolicyCreateRequest policyCreateRequest) {
        return MAPPER.map(policyCreateRequest, Policy.class);
    }
}
