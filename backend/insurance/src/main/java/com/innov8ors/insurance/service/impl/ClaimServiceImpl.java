package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.enums.PolicyStatus;
import com.innov8ors.insurance.exception.BadRequestException;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.ClaimRepository;
import com.innov8ors.insurance.repository.UserPolicyRepository;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {
    private final ClaimRepository claimRepository;
    private final UserPolicyRepository userPolicyRepository;

    @Override
    public ClaimResponse submitClaim(ClaimCreateRequest request, Long userId) {
        log.info("Submitting claim for user ID: {} and policy ID: {}", userId, request.getUserPolicyId());

        // Verify that the user policy exists and belongs to the user
        UserPolicy userPolicy = userPolicyRepository.findByIdAndUserId(request.getUserPolicyId(), userId)
                .orElseThrow(() -> new NotFoundException("User policy not found or does not belong to the user"));

        // Business Rule: Claims allowed only for ACTIVE policies
        if (userPolicy.getStatus() != PolicyStatus.ACTIVE) {
            throw new BadRequestException("Claims can only be submitted for active policies");
        }

        // Validate claim date (should not be in the future and within policy period)
        if (request.getClaimDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Claim date cannot be in the future");
        }

        if (request.getClaimDate().isBefore(userPolicy.getStartDate().toLocalDate()) ||
                request.getClaimDate().isAfter(userPolicy.getEndDate().toLocalDate())) {
            throw new BadRequestException("Claim date must be within the policy coverage period");
        }

        // Create new claim with PENDING status
        Claim claim = new Claim(
                request.getUserPolicyId(),
                request.getClaimDate(),
                request.getClaimAmount(),
                request.getReason()
        );

        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim submitted successfully with ID: {}", savedClaim.getId());

        return mapToClaimResponse(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getUserClaims(Long userId) {
        log.info("Fetching claims for user ID: {}", userId);

        List<Claim> claims = claimRepository.findByUserId(userId);
        return claims.stream()
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request) {
        log.info("Updating claim status for claim ID: {} to status: {}", claimId, request.getStatus());

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new NotFoundException("Claim not found with ID: " + claimId));

        // Business Rule: Can only update claims that are in PENDING status
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException("Can only update claims with PENDING status");
        }

        // Validate status transition
        if (request.getStatus() == ClaimStatus.PENDING) {
            throw new BadRequestException("Cannot set status back to PENDING");
        }

        claim.setStatus(request.getStatus());
        claim.setReviewerComment(request.getReviewerComment());
        claim.setResolvedDate(LocalDate.now());

        Claim updatedClaim = claimRepository.save(claim);
        log.info("Claim status updated successfully for claim ID: {}", claimId);

        return mapToClaimResponse(updatedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimByIdAndUserId(Long claimId, Long userId) {
        log.info("Fetching claim ID: {} for user ID: {}", claimId, userId);

        Claim claim = claimRepository.findByIdAndUserPolicyUserId(claimId, userId)
                .orElseThrow(() -> new NotFoundException("Claim not found or does not belong to the user"));

        return mapToClaimResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        log.info("Fetching all claims for admin");

        List<Claim> claims = claimRepository.findAll();
        return claims.stream()
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getPendingClaims() {
        log.info("Fetching all pending claims for admin");

        List<Claim> pendingClaims = claimRepository.findByStatus(ClaimStatus.PENDING);
        return pendingClaims.stream()
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimResponse> getApprovedClaims() {
        log.info("Fetching all approved claims for admin");

        List<Claim> approvedClaims = claimRepository.findByStatus(ClaimStatus.APPROVED);
        return approvedClaims.stream()
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    private ClaimResponse mapToClaimResponse(Claim claim) {
        ClaimResponse.ClaimResponseBuilder builder = ClaimResponse.builder()
                .id(claim.getId())
                .userPolicyId(claim.getUserPolicyId())
                .claimDate(claim.getClaimDate())
                .claimAmount(claim.getClaimAmount())
                .reason(claim.getReason())
                .status(claim.getStatus())
                .reviewerComment(claim.getReviewerComment())
                .resolvedDate(claim.getResolvedDate());

        // Add policy information if available
        if (claim.getUserPolicy() != null && claim.getUserPolicy().getPolicy() != null) {
            builder.policyName(claim.getUserPolicy().getPolicy().getName())
                    .policyType(claim.getUserPolicy().getPolicy().getType());
        }

        return builder.build();
    }


}
