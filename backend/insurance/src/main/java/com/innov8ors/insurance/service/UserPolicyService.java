package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.request.UserPolicyUpdateRequest;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;


public interface UserPolicyService {


    @Transactional
    UserPolicyResponse purchasePolicy(String userEmail, PolicyPurchaseRequest request);

    UserPolicyPaginatedResponse getUserPolicies(String userEmail, Integer page, Integer size);


    // Overloaded method to maintain backward compatibility with Long userId
    @Transactional
    UserPolicyResponse purchasePolicy(Long userId, PolicyPurchaseRequest request);

    UserPolicy makePayment(String userEmail, Long policyId);

    boolean isExistsByUserIdAndPolicyId(Long userId, Long policyId);


    UserPolicy getByUserIdAndPolicyId(Long userId, Long policyId);

    Page<UserPolicyResponse> getUsersByPolicyId(Long policyId, int page, int size);

    UserPolicy updateUserPolicy(Long userId, Long policyId, UserPolicyUpdateRequest userPolicyUpdateRequest);

    UserPolicyResponse renewPolicy(Long userId, Long policyId);

    UserPolicyPaginatedResponse getRenewablePolicies(Long userId, Integer page, Integer size);

    UserPolicy getById(Long userPolicyId);
}