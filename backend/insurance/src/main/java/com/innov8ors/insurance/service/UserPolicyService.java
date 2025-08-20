package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


public interface UserPolicyService {


    @Transactional
    UserPolicy purchasePolicy(String userEmail, PolicyPurchaseRequest request);

    UserPolicyPaginatedResponse getUserPolicies(String userEmail, Integer page, Integer size);


    // Overloaded method to maintain backward compatibility with Long userId
    @Transactional
    UserPolicy purchasePolicy(Long userId, PolicyPurchaseRequest request);

    UserPolicy makePayment(String userEmail, Long policyId);

    boolean isExistsByUserIdAndPolicyId(Long userId, Long policyId);


    UserPolicy getByUserIdAndPolicyId(Long userId, Long policyId);

    Page<UserPolicyResponse> getUsersByPolicyId(Long policyId, int page, int size);

    UserPolicy updateUserPolicy(Long userId, Long policyId, BigDecimal claimAmount);
}