package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.exception.AlreadyExistsException;
import com.innov8ors.insurance.mapper.UserPolicyMapper;
import com.innov8ors.insurance.repository.dao.UserPolicyDao;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.request.UserPolicyUpdateRequest;
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
    public UserPolicy purchasePolicy(String userEmail, PolicyPurchaseRequest request) {
        log.debug("Received request to purchase policy for user: {}", userEmail);
        User user = userService.getByEmail(userEmail);
        return purchasePolicy(user.getId(), request);
    }

    @Override
    public Page<UserPolicyResponse> getUserPolicies(String userEmail, int page, int size) {
        log.debug("Fetching policies for user: {}", userEmail);
        User user = userService.getByEmail(userEmail);

        Page<UserPolicy> userPolicies = userPolicyDao.findByUserIdWithPolicy(user.getId(), PageRequest.of(page, size, Sort.by(START_DATE_PLACEHOLDER).descending()));


        return userPolicies.map(UserPolicyMapper::convertToResponse);
    }

    @Transactional
    @Override
    public UserPolicy purchasePolicy(Long userId, PolicyPurchaseRequest request) {
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

        return userPolicy;
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
    public UserPolicy updateUserPolicy(Long userId, Long policyId, UserPolicyUpdateRequest userPolicyUpdateRequest) {
        log.debug("Updating user policy for user ID: {}, policy ID: {}", userId, policyId);
        UserPolicy userPolicy = getByUserIdAndPolicyId(userId, policyId);

        if (userPolicyUpdateRequest.getStartDate() != null) {
            userPolicy.setStartDate(userPolicyUpdateRequest.getStartDate());
        }
        if (userPolicyUpdateRequest.getEndDate() != null) {
            userPolicy.setEndDate(userPolicyUpdateRequest.getEndDate());
        }
        if (userPolicyUpdateRequest.getStatus() != null) {
            userPolicy.setStatus(userPolicyUpdateRequest.getStatus());
        }
        if (userPolicyUpdateRequest.getPremiumPaid() != null) {
            userPolicy.setPremiumPaid(userPolicyUpdateRequest.getPremiumPaid());
        }
        if (userPolicyUpdateRequest.getTotalAmountClaimed() != null) {
            userPolicy.setTotalAmountClaimed(userPolicyUpdateRequest.getTotalAmountClaimed());
        }
        log.info("Saving updated user policy for user ID: {}, policy ID: {}", userId, policyId);

        return userPolicyDao.persist(userPolicy);
    }
}
