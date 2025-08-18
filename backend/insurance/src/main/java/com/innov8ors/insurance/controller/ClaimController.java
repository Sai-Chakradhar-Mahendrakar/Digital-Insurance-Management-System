package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping("/claim")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> submitClaim(
            @Valid @RequestBody ClaimCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("Received claim submission request from user: {}", userPrincipal.getId());

        ClaimResponse response = claimService.submitClaim(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/claims")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ClaimResponse>> getUserClaims(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("Fetching claims for user: {}", userPrincipal.getId());

        List<ClaimResponse> claims = claimService.getUserClaims(userPrincipal.getId());
        return ResponseEntity.ok(claims);
    }

    @PutMapping("/claim/{claimId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> updateClaimStatus(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimStatusUpdateRequest request) {

        log.info("Admin updating claim status for claim ID: {} to status: {}", claimId, request.getStatus());

        ClaimResponse response = claimService.updateClaimStatus(claimId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> getClaimById(
            @PathVariable Long claimId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("Fetching claim ID: {} for user: {}", claimId, userPrincipal.getId());

        ClaimResponse response = claimService.getClaimByIdAndUserId(claimId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {

        log.info("Admin fetching all claims");

        List<ClaimResponse> claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/admin/claims/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClaimResponse>> getPendingClaims() {

        log.info("Admin fetching all pending claims");

        List<ClaimResponse> pendingClaims = claimService.getPendingClaims();
        return ResponseEntity.ok(pendingClaims);
    }
}