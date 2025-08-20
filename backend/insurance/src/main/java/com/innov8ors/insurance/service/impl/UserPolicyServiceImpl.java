package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.mapper.UserPolicyMapper;
import com.innov8ors.insurance.repository.dao.UserPolicyDao;
import com.innov8ors.insurance.repository.dao.UserDao;
import com.innov8ors.insurance.repository.dao.PolicyDao;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
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

import static com.innov8ors.insurance.util.Constant.UserPolicyConstants.START_DATE_PLACEHOLDER;

@Service
@Slf4j
public class UserPolicyServiceImpl implements UserPolicyService {

    private final UserPolicyDao userPolicyDao;
    private final UserService userService;
    private final PolicyService policyService;
    private final UserDao userDao;
    private final PolicyDao policyDao;

    public UserPolicyServiceImpl(UserPolicyDao userPolicyDao,
                                 UserService userService,
                                 PolicyService policyService,
                                 UserDao userDao,
                                 PolicyDao policyDao) {
        this.userPolicyDao = userPolicyDao;
        this.userService = userService;
        this.policyService = policyService;
        this.userDao = userDao;
        this.policyDao = policyDao;
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
            throw new RuntimeException("User already has this policy");
        }

        UserPolicy userPolicy = getUserPolicy(userId, request, policy);

        log.info("Creating new user policy for user ID: {}, policy ID: {}", userId, request.getPolicyId());
        userPolicy = userPolicyDao.save(userPolicy);

        // Manually load the relationships
        User user = userDao.findById(userId).orElse(null);
        Policy policyEntity = policyDao.findById(request.getPolicyId()).orElse(null);

        userPolicy.setUser(user);
        userPolicy.setPolicy(policyEntity);

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
}
