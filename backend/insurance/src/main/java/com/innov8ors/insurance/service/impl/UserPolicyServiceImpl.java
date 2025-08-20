package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.exception.AlreadyExistsException;
import com.innov8ors.insurance.mapper.UserPolicyMapper;
import com.innov8ors.insurance.repository.dao.UserPolicyDao;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_ALREADY_HAS_POLICY;
import static com.innov8ors.insurance.util.Constant.UserPolicyConstants.START_DATE_PLACEHOLDER;

@Service
@Slf4j
public class UserPolicyServiceImpl implements UserPolicyService {

    private final UserPolicyDao userPolicyDao;
    private final UserService userService;
    private final PolicyService policyService;

    public UserPolicyServiceImpl(UserPolicyDao userPolicyDao,
                                 UserService userService,
                                 PolicyService policyService) {
        this.userPolicyDao = userPolicyDao;
        this.userService = userService;
        this.policyService = policyService;
    }

    @Transactional
    @Override
    public UserPolicyResponse purchasePolicy(String userEmail, PolicyPurchaseRequest request) {
        log.debug("Received request to purchase policy for user: {}", userEmail);
        User user = userService.getByEmail(userEmail);
        return purchasePolicy(user.getId(), request);
    }

    @Override
    public UserPolicyPaginatedResponse getUserPolicies(String userEmail, Integer page, Integer size) {
        log.debug("Fetching policies for user: {}", userEmail);
        User user = userService.getByEmail(userEmail);

        Page<UserPolicy> userPolicies = userPolicyDao.findByUserIdWithPolicy(user.getId(), PageRequest.of(page, size, Sort.by(START_DATE_PLACEHOLDER).descending()));


        return UserPolicyMapper.getPolicyPaginatedResponse(userPolicies, page, size);
    }

    @Transactional
    @Override
    public UserPolicyResponse purchasePolicy(Long userId, PolicyPurchaseRequest request) {
        Policy policy = policyService.getById(request.getPolicyId());

        if (isExistsByUserIdAndPolicyId(userId, request.getPolicyId())) {
            log.error("User with ID {} already has policy with ID {}", userId, request.getPolicyId());
            throw new AlreadyExistsException(USER_ALREADY_HAS_POLICY);
        }

        UserPolicy userPolicy = getUserPolicy(userId, request, policy);

        log.info("Creating new user policy for user ID: {}, policy ID: {}", userId, request.getPolicyId());
        userPolicy = userPolicyDao.save(userPolicy);

        // Manually load the relationships
        User user = userService.getById(userId);

        userPolicy.setUser(user);
        userPolicy.setPolicy(policy);

        return UserPolicyMapper.convertToResponse(userPolicy);
    }

    @Override
    public UserPolicy makePayment(String userEmail, Long policyId) {
        return null;
    }

    private UserPolicy getUserPolicy(Long userId, PolicyPurchaseRequest request, Policy policy) {
        return UserPolicy.builder()
                .userId(userId)
                .policyId(request.getPolicyId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(policy.getDurationMonths()))
                .status(UserPolicyStatus.ACTIVE)
                .premiumPaid(request.getPremiumPaid())
                .build();
    }

    @Transactional
    // todo: Add a scheduled task to run this method periodically
    public void updateExpiredPolicies() {
        List<UserPolicy> expiredPolicies = userPolicyDao
                .findByStatusAndEndDateBefore(UserPolicyStatus.ACTIVE, LocalDateTime.now());

        expiredPolicies.forEach(policy -> {
            policy.setStatus(UserPolicyStatus.EXPIRED);
            userPolicyDao.save(policy);
        });
    }

    public boolean isExistsByUserIdAndPolicyId(Long userId, Long policyId) {
        return userPolicyDao.existsByUserIdAndPolicyId(userId, policyId);
    }

    @Override
    public UserPolicy getByUserIdAndPolicyId(Long userId, Long policyId) {

        return userPolicyDao.findByUserIdAndPolicyId(userId, policyId)
                .orElseThrow(() -> {
                    log.error("User Policy not found for user ID: {} and policy ID: {}", userId, policyId);
                    return new RuntimeException("User Policy not found for user ID: " + userId + " and policy ID: " + policyId);
                });
    }

    @Override
    public Page<UserPolicyResponse> getUsersByPolicyId(Long policyId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserPolicy> userPolicies = userPolicyDao.findByPolicyIdWithUser(policyId, pageRequest);

        return userPolicies.map(userPolicy -> UserPolicyResponse.builder()
                .id(userPolicy.getId())
                .policyId(userPolicy.getPolicyId())
                .policyName(userPolicy.getPolicy().getName())
                .policyType(userPolicy.getPolicy().getType())
                .userId(userPolicy.getUser().getId())
                .userName(userPolicy.getUser().getName())
                .userEmail(userPolicy.getUser().getEmail())
                .userPhone(userPolicy.getUser().getPhone())
                .userAddress(userPolicy.getUser().getAddress())
                .startDate(userPolicy.getStartDate())
                .endDate(userPolicy.getEndDate())
                .status(userPolicy.getStatus())
                .premiumPaid(userPolicy.getPremiumPaid())
                .build());
    }

    @Override
    public UserPolicy updateUserPolicy(Long userId, Long policyId, BigDecimal claimAmount) {
        log.debug("Updating user policy for user ID: {}, policy ID: {}", userId, policyId);
        UserPolicy userPolicy = getByUserIdAndPolicyId(userId, policyId);

        Policy policy = userPolicy.getPolicy();
        if (policy == null) {
            log.error("Policy not found for user policy ID: {}", userPolicy.getId());
            throw new RuntimeException("Policy not found for user policy ID: " + userPolicy.getId());
        }

        BigDecimal coverageAmount = policy.getCoverageAmount();
        BigDecimal currentClaimed = userPolicy.getTotalAmountClaimed() != null
                ? userPolicy.getTotalAmountClaimed()
                : BigDecimal.ZERO;

        if (claimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Claim amount must be positive");
        }

        BigDecimal remainingAmount = coverageAmount.subtract(currentClaimed);

        if (claimAmount.compareTo(remainingAmount) > 0) {
            throw new RuntimeException("Claim amount " + claimAmount +
                    " exceeds remaining coverage of " + remainingAmount);
        }

        BigDecimal newTotalClaimed = currentClaimed.add(claimAmount);
        userPolicy.setTotalAmountClaimed(newTotalClaimed);

        log.debug("Updated total claimed from {} to {}, remaining: {}",
                currentClaimed, newTotalClaimed, coverageAmount.subtract(newTotalClaimed));

        return userPolicyDao.save(userPolicy);
    }
}
