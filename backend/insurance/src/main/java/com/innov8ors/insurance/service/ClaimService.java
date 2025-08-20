package com.innov8ors.insurance.service;

import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;

public interface ClaimService {
    ClaimResponse submitClaim(ClaimCreateRequest request, Long userId);

    ClaimPaginatedResponse getUserClaims(Long userId, Integer page, Integer size);

    ClaimResponse updateClaimStatus(Long userId, Long claimId, ClaimStatusUpdateRequest request);

    ClaimResponse getByClaimIdAndUserId(Long claimId, Long userId);

    ClaimPaginatedResponse getAllClaims(ClaimStatus claimStatus, Integer page, Integer size);
}
