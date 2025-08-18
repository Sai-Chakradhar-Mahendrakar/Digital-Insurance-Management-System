package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import org.springframework.data.domain.Page;

public interface PolicyService {
    Page<Policy> getPolicies(String type, Integer page, Integer size);

    Policy addPolicy(PolicyCreateRequest policyCreateRequest);
}
