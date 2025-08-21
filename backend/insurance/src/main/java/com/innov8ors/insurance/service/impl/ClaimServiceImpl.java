package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.exception.BadRequestException;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.dao.ClaimDao;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.request.UserPolicyUpdateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.util.NotificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.innov8ors.insurance.mapper.NotificationMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.innov8ors.insurance.mapper.ClaimMapper.getClaimFromRequest;
import static com.innov8ors.insurance.mapper.ClaimMapper.getClaimPaginatedResponse;
import static com.innov8ors.insurance.mapper.ClaimMapper.mapToClaimResponse;
import static com.innov8ors.insurance.util.Constant.ClaimConstants.CLAIM_DATE_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.ClaimConstants.CLAIM_STATUS_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CANNOT_SET_STATUS_BACK_TO_PENDING;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIMS_ALLOWED_ONLY_FOR_ACTIVE_POLICIES;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIM_AMOUNT_EXCEEDS_POLICY_COVERAGE;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIM_DATE_NOT_WITHIN_POLICY_COVERAGE;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIM_NOT_FOUND;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.ONLY_PENDING_CLAIMS_CAN_BE_UPDATED;

@Service
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimDao claimDao;

    private final UserPolicyService userPolicyService;

    private final PolicyService policyService;
    private final NotificationService notificationService;

    public ClaimServiceImpl(ClaimDao claimDao,
                            UserPolicyService userPolicyService,
                            PolicyService policyService,
                            NotificationService notificationService) {
        this.claimDao = claimDao;
        this.userPolicyService = userPolicyService;
        this.policyService = policyService;
        this.notificationService = notificationService;
    }

    @Override
    public ClaimResponse submitClaim(ClaimCreateRequest request, Long userId) {
        log.info("Submitting claim for user ID: {} and policy ID: {}", userId, request.getPolicyId());
        UserPolicy userPolicy = userPolicyService.getByUserIdAndPolicyId(userId, request.getPolicyId());

        Policy policy = policyService.getById(request.getPolicyId());

        validateClaimRequest(request, userPolicy, policy);

        Claim claim = getClaimFromRequest(request, userPolicy);

        Claim savedClaim = claimDao.persist(claim);
        savedClaim.setUserPolicy(userPolicy);
        log.info("Claim submitted successfully with ID: {}", savedClaim.getId());
        return mapToClaimResponse(savedClaim);
    }

    private void validateClaimRequest(ClaimCreateRequest request, UserPolicy userPolicy, Policy policy) {
        if (userPolicy.getStatus() != UserPolicyStatus.ACTIVE  &&
        userPolicy.getStatus() != UserPolicyStatus.RENEWED) {
            log.error("Claims can only be submitted for active policies. User Policy ID: {}", userPolicy.getId());
            throw new BadRequestException(CLAIMS_ALLOWED_ONLY_FOR_ACTIVE_POLICIES);
        }

        if(request.getClaimAmount().compareTo(policy.getCoverageAmount().subtract(userPolicy.getTotalAmountClaimed())) > 0) {
            log.error("Claim amount exceeds policy coverage. Claim Amount: {}, Policy Coverage: {}",
                      request.getClaimAmount(), policy.getCoverageAmount());
            throw new BadRequestException(CLAIM_AMOUNT_EXCEEDS_POLICY_COVERAGE);
        }

        if (request.getClaimDate().isBefore(userPolicy.getStartDate().toLocalDate()) ||
                request.getClaimDate().isAfter(userPolicy.getEndDate().toLocalDate()) ||
                request.getClaimDate().isAfter(LocalDate.now())) {
            throw new BadRequestException(CLAIM_DATE_NOT_WITHIN_POLICY_COVERAGE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimPaginatedResponse getUserClaims(Long userId, Integer page, Integer size) {
        log.info("Fetching claims for user ID: {}", userId);

        Page<Claim> claims = claimDao.getByUserId(userId, PageRequest.of(page, size, Sort.by(CLAIM_DATE_PLACEHOLDER)));
        return getClaimPaginatedResponse(page, size, claims);
    }

    @Override
    public ClaimResponse updateClaimStatus(Long userId, Long claimId, ClaimStatusUpdateRequest claimStatusUpdateRequest) {
        log.info("Updating claim status for claim ID: {} to status: {}", claimId, claimStatusUpdateRequest.getStatus());

        Claim claim = checkAndGetClaim(claimId);

        UserPolicy existingUserPolicy = userPolicyService.getById(claim.getUserPolicyId());

        Policy policy = policyService.getById(existingUserPolicy.getPolicyId());

        validateClaimUpdateRequest(claimStatusUpdateRequest, claim);

        updateClaimStatus(claimStatusUpdateRequest, claim);

        Claim updatedClaim = claimDao.persist(claim);
        log.info("Claim status updated successfully for claim ID: {}", claimId);

        UserPolicy updatedUserPolicy = updateUserPolicyAfterClaimSubmission(updatedClaim, existingUserPolicy);

        sendNotification(updatedClaim, userId);

        updateOtherClaims(userId, claimId, claim.getUserPolicyId(), policy, updatedUserPolicy);

        return mapToClaimResponse(updatedClaim);
    }

    private void sendNotification(Claim claim, Long userId) {
        NotificationUtil.send(notificationService, userId, "Your claim " + claim.getId() + " status has been updated to " + claim.getStatus(), NotificationType.CLAIM_UPDATE);
    }

    private void updateOtherClaims(Long userId, Long claimId, Long userPolicyId, Policy policy, UserPolicy userPolicy) {
        List<Claim> otherClaims = claimDao.getByUserPolicyId(userPolicyId);
        otherClaims.forEach(claim -> {
                    if(Objects.equals(claim.getId(), claimId)) {
                        return;
                    }
                    if(claim.getClaimAmount().compareTo(policy.getCoverageAmount().subtract(userPolicy.getTotalAmountClaimed())) > 0) {
                        claim.setStatus(ClaimStatus.REJECTED);
                        claim.setReviewerComment("Claim amount exceeds policy coverage after this claim.");
                        claimDao.persist(claim);
                        sendNotification(claim, userId);
                    }
                });
    }

    private void updateClaimStatus(ClaimStatusUpdateRequest request, Claim claim) {
        if(request.getStatus() != null) {
            if(request.getStatus() == ClaimStatus.APPROVED) {
                claim.setResolvedDate(LocalDate.now());
            }
            claim.setStatus(request.getStatus());
        }
        if(request.getReviewerComment() != null) {
            claim.setReviewerComment(request.getReviewerComment());
        }
    }

    private void validateClaimUpdateRequest(ClaimStatusUpdateRequest request, Claim claim) {
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException(ONLY_PENDING_CLAIMS_CAN_BE_UPDATED);
        }

        if (request.getStatus() == ClaimStatus.PENDING) {
            throw new BadRequestException(CANNOT_SET_STATUS_BACK_TO_PENDING);
        }
    }

    private UserPolicy updateUserPolicyAfterClaimSubmission(Claim updatedClaim, UserPolicy userPolicy) {
        if(updatedClaim.getStatus() != ClaimStatus.APPROVED) {
            return userPolicy;
        }
        UserPolicyUpdateRequest userPolicyUpdateRequest = UserPolicyUpdateRequest.builder()
                .totalAmountClaimed(userPolicy.getTotalAmountClaimed().add(updatedClaim.getClaimAmount()))
                .build();
        return userPolicyService.updateUserPolicy(userPolicy.getUserId(), userPolicy.getPolicyId(), userPolicyUpdateRequest);
    }

    private Claim checkAndGetClaim(Long claimId) {
        return claimDao.findById(claimId)
                .orElseThrow(() -> new NotFoundException(CLAIM_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getByClaimIdAndUserId(Long claimId, Long userId) {
        log.info("Fetching claim ID: {} for user ID: {}", claimId, userId);

        Claim claim = claimDao.getByClaimIdAndUserId(claimId, userId)
                .orElseThrow(() -> new NotFoundException(CLAIM_NOT_FOUND));

        return mapToClaimResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimPaginatedResponse getAllClaims(ClaimStatus status, Integer page, Integer size) {
        log.info("Fetching all claims for admin");
        Specification<Claim> claimSpecification= getClaimSpecification(status);
        Pageable pageable = PageRequest.of(page, size, Sort.by(CLAIM_DATE_PLACEHOLDER).descending());
        Page<Claim> claims = claimDao.findAll(claimSpecification, pageable);
        return getClaimPaginatedResponse(page, size, claims);
    }


    private Specification<Claim> getClaimSpecification(ClaimStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status != null) {
                return criteriaBuilder.equal(root.get(CLAIM_STATUS_PLACEHOLDER), status);
            }
            return criteriaBuilder.conjunction();
        };
    }
}
