package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserPolicyService {


    @Transactional
    UserPolicy purchasePolicy(String userEmail, PolicyPurchaseRequest request);

    Page<UserPolicyResponse> getUserPolicies(String userEmail, int page, int size);


    // Overloaded method to maintain backward compatibility with Long userId
    @Transactional
    UserPolicy purchasePolicy(Long userId, PolicyPurchaseRequest request);

    UserPolicy makePayment(String userEmail, Long policyId);
}