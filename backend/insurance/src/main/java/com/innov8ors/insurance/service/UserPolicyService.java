package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.enums.PolicyStatus;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.repository.PolicyRepository;
import com.innov8ors.insurance.repository.UserPolicyRepository;
import com.innov8ors.insurance.repository.UserRepository;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPolicyService {

    private final UserPolicyRepository userPolicyRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    public UserPolicyService(UserPolicyRepository userPolicyRepository,
                             PolicyRepository policyRepository,
                             UserRepository userRepository) {
        this.userPolicyRepository = userPolicyRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserPolicy purchasePolicy(String userEmail, PolicyPurchaseRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        // Check if user already has this policy
        if (userPolicyRepository.existsByUserIdAndPolicyId(user.getId(), request.getPolicyId())) {
            throw new RuntimeException("User already has this policy");
        }

        // Create new user policy
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusYears(1); // Assuming 1-year policy

        UserPolicy userPolicy = new UserPolicy(
                user.getId(),
                request.getPolicyId(),
                startDate,
                endDate,
                PolicyStatus.ACTIVE,
                request.getPremiumPaid()
        );

        return userPolicyRepository.save(userPolicy);
    }

    public Page<UserPolicyResponse> getUserPolicies(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<UserPolicy> userPolicies = userPolicyRepository.findByUserIdWithPolicy(user.getId(), pageable);

        return userPolicies.map(this::convertToResponse);
    }

    // Overloaded method to maintain backward compatibility with Long userId
    @Transactional
    public UserPolicy purchasePolicy(Long userId, PolicyPurchaseRequest request) {
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        // Check if user already has this policy
        if (userPolicyRepository.existsByUserIdAndPolicyId(userId, request.getPolicyId())) {
            throw new RuntimeException("User already has this policy");
        }

        // Create new user policy
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusYears(1); // Assuming 1-year policy

        UserPolicy userPolicy = new UserPolicy(
                userId,
                request.getPolicyId(),
                startDate,
                endDate,
                PolicyStatus.ACTIVE,
                request.getPremiumPaid()
        );

        return userPolicyRepository.save(userPolicy);
    }

    public Page<UserPolicyResponse> getUserPolicies(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserPolicy> userPolicies = userPolicyRepository.findByUserIdWithPolicy(userId, pageable);

        return userPolicies.map(this::convertToResponse);
    }

    @Transactional
    public void updateExpiredPolicies() {
        List<UserPolicy> expiredPolicies = userPolicyRepository
                .findByStatusAndEndDateBefore(PolicyStatus.ACTIVE, LocalDateTime.now());

        expiredPolicies.forEach(policy -> {
            policy.setStatus(PolicyStatus.EXPIRED);
            userPolicyRepository.save(policy);
        });
    }

    private UserPolicyResponse convertToResponse(UserPolicy userPolicy) {
        return new UserPolicyResponse(
                userPolicy.getId(),
                userPolicy.getPolicyId(),
                userPolicy.getPolicy().getName(),
                userPolicy.getPolicy().getType(),
                userPolicy.getStartDate(),
                userPolicy.getEndDate(),
                userPolicy.getStatus(),
                userPolicy.getPremiumPaid()
        );
    }
}