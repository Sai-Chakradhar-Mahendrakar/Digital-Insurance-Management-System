package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.response.ClaimResponse;

import java.util.List;

public interface ClaimService {
    ClaimResponse submitClaim(ClaimCreateRequest request, Long userId);

    List<ClaimResponse> getUserClaims(Long userId);

    ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request);

    ClaimResponse getClaimByIdAndUserId(Long claimId, Long userId);

    List<ClaimResponse> getAllClaims();

    List<ClaimResponse> getPendingClaims();

    List<ClaimResponse> getApprovedClaims();
}
