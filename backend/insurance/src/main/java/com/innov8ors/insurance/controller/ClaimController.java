package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping("/claim")
    public ResponseEntity<ClaimResponse> submitClaim(@Valid @RequestBody ClaimCreateRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("Received claim submission request from user: {}", userPrincipal.getId());
        ClaimResponse response = claimService.submitClaim(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/claims")
    public ResponseEntity<ClaimPaginatedResponse> getUserClaims(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Fetching claims for user: {}", userPrincipal.getId());
        ClaimPaginatedResponse claims = claimService.getUserClaims(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(claims);
    }

    @PutMapping("/claim/{claimId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> updateClaimStatus(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable Long claimId,
                                                           @Valid @RequestBody ClaimStatusUpdateRequest request) {
        log.info("Admin updating claim status for claim ID: {} to status: {}", claimId, request.getStatus());
        ClaimResponse response = claimService.updateClaimStatus(userPrincipal.getId(), claimId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long claimId,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("Fetching claim ID: {} for user: {}", claimId, userPrincipal.getId());
        ClaimResponse response = claimService.getByClaimIdAndUserId(claimId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimPaginatedResponse> getAllClaims(@RequestParam(required = false) Optional<ClaimStatus> status,
                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Admin fetching all claims");
        ClaimPaginatedResponse claims = claimService.getAllClaims(status.orElse(null), page, size);
        return ResponseEntity.ok(claims);
    }
}