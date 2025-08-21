package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.exception.BadRequestException;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.exception.AlreadyExistsException;
import com.innov8ors.insurance.mapper.UserPolicyMapper;
import com.innov8ors.insurance.repository.dao.UserPolicyDao;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.request.UserPolicyUpdateRequest;
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
import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.*;
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

        UserPolicy userPolicy = checkAndGetExistingUserPolicy(userId, request, policy);

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

    private UserPolicy checkAndGetExistingUserPolicy(Long userId, PolicyPurchaseRequest request, Policy policy) {
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

    @Transactional
    @Override
    public UserPolicyResponse renewPolicy(Long userId, Long policyId) {
        log.debug("Received request to renew policy ID: {} for user: {}", policyId, userId);
        UserPolicy existingUserPolicy = checkAndGetExistingUserPolicy(userId, policyId);
        Policy policy = policyService.getById(policyId);

        validatePolicyForRenewal(existingUserPolicy);
        UserPolicy renewedUserPolicy = updateUserPolicyForRenewal(existingUserPolicy, policy);

        UserPolicy savedPolicy = userPolicyDao.persist(renewedUserPolicy);
        log.info("Policy ID: {} renewed successfully for user ID: {}", policyId, userId);
        return UserPolicyMapper.convertToResponse(savedPolicy);
    }

    private void validatePolicyForRenewal(UserPolicy existingUserPolicy) {

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = currentTime.plusDays(30);

        boolean isExpired = existingUserPolicy.getStatus() == UserPolicyStatus.EXPIRED;
        boolean isActiveAndExpiringWithin30Days = existingUserPolicy.getStatus() == UserPolicyStatus.ACTIVE &&
                existingUserPolicy.getEndDate().isBefore(thirtyDaysFromNow);

        if (!isExpired && !isActiveAndExpiringWithin30Days) {
            log.error("Policy ID: {} is not eligible for renewal. Status: {}, End Date: {}",
                    existingUserPolicy.getPolicyId(), existingUserPolicy.getStatus(), existingUserPolicy.getEndDate());
            throw new BadRequestException(NOT_ELIGIBLE_FOR_RENEWAL);
        }

        log.debug("Policy ID: {} is eligible for renewal. Status: {}, End Date: {}",
                existingUserPolicy.getPolicyId(), existingUserPolicy.getStatus(), existingUserPolicy.getEndDate());
    }

    private UserPolicy checkAndGetExistingUserPolicy(Long userId, Long policyId) {
        Optional<UserPolicy> existingPolicyOptional = userPolicyDao.findByUserIdAndPolicyId(userId, policyId);
        return existingPolicyOptional.orElseThrow(() -> {
            log.error(POLICY_NOT_FOUND_OR_DOESNT_BELONG_TO_USER);
            return new NotFoundException(POLICY_NOT_FOUND_OR_DOESNT_BELONG_TO_USER);
        });
    }

    @Override
    public UserPolicyPaginatedResponse getRenewablePolicies(Long userId, Integer page, Integer size) {
        log.debug("Fetching renewable policies for user: {}", userId);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = currentTime.plusDays(30);

        Page<UserPolicy> renewablePolicies = userPolicyDao.findActiveNearingExpiryOrExpiredPolicies(
                userId, currentTime, thirtyDaysFromNow, PageRequest.of(page, size, Sort.by(START_DATE_PLACEHOLDER).descending()));

        return UserPolicyMapper.getPolicyPaginatedResponse(renewablePolicies, page, size);
    }

    private UserPolicy updateUserPolicyForRenewal(UserPolicy existingUserPolicy, Policy policy) {
        BigDecimal premiumAmount = policy.getPremiumAmount();
        BigDecimal renewalRate = policy.getRenewalPremiumRate();
        BigDecimal renewalPremium = premiumAmount.add(premiumAmount.multiply(renewalRate));
        existingUserPolicy.setStartDate(LocalDateTime.now());
        existingUserPolicy.setEndDate(LocalDateTime.now().plusMonths(policy.getDurationMonths()));
        existingUserPolicy.setStatus(UserPolicyStatus.ACTIVE);
        existingUserPolicy.setTotalAmountClaimed(BigDecimal.ZERO);
        existingUserPolicy.setPremiumPaid(existingUserPolicy.getPremiumPaid().add(renewalPremium));
        return existingUserPolicy;
    }
}
