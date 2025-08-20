package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import org.springframework.data.domain.Page;

public class ClaimMapper {
    public static ClaimResponse mapToClaimResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .userPolicyId(claim.getUserPolicyId())
                .claimDate(claim.getClaimDate())
                .claimAmount(claim.getClaimAmount())
                .reason(claim.getReason())
                .status(claim.getStatus())
                .reviewerComment(claim.getReviewerComment())
                .resolvedDate(claim.getResolvedDate())
                .policyName(claim.getUserPolicy().getPolicy().getName())
                .policyType(claim.getUserPolicy().getPolicy().getType())
                .build();
    }

    public static ClaimPaginatedResponse getClaimPaginatedResponse(Integer page, Integer size, Page<Claim> claims) {
        return ClaimPaginatedResponse.builder()
                .claims(claims.getContent()
                        .stream()
                        .map(ClaimMapper::mapToClaimResponse)
                        .toList())
                .page(page)
                .size(size)
                .totalPages(claims.getTotalPages())
                .totalElements(claims.getTotalElements())
                .build();
    }

    public static Claim getClaimFromRequest(ClaimCreateRequest request, UserPolicy userPolicy) {
        return Claim.builder()
                .userPolicyId(userPolicy.getId())
                .claimDate(request.getClaimDate())
                .claimAmount(request.getClaimAmount())
                .reason(request.getReason())
                .status(ClaimStatus.PENDING)
                .build();
    }
}
