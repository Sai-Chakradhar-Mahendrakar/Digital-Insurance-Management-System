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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {
    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserPolicyRepository userPolicyRepository;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private UserPolicy activeUserPolicy;
    private ClaimCreateRequest claimCreateRequest;
    private Claim pendingClaim;

    @BeforeEach
    void setUp() {
        activeUserPolicy = new UserPolicy();
        activeUserPolicy.setId(1L);
        activeUserPolicy.setUserId(1L);
        activeUserPolicy.setPolicyId(1L);
        activeUserPolicy.setStatus(PolicyStatus.ACTIVE);
        activeUserPolicy.setStartDate(LocalDateTime.now().minusMonths(6));
        activeUserPolicy.setEndDate(LocalDateTime.now().plusMonths(6));

        claimCreateRequest = ClaimCreateRequest.builder()
                .userPolicyId(1L)
                .claimDate(LocalDate.now().minusDays(10))
                .claimAmount(new BigDecimal("5000.00"))
                .reason("Medical emergency treatment")
                .build();

        pendingClaim = new Claim();
        pendingClaim.setId(1L);
        pendingClaim.setUserPolicyId(1L);
        pendingClaim.setClaimDate(LocalDate.now().minusDays(10));
        pendingClaim.setClaimAmount(new BigDecimal("5000.00"));
        pendingClaim.setReason("Medical emergency treatment");
        pendingClaim.setStatus(ClaimStatus.PENDING);
    }

    @Test
    void testSuccessfulSubmitClaim() {
        // Given
        when(userPolicyRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(activeUserPolicy));
        when(claimRepository.save(any(Claim.class)))
                .thenReturn(pendingClaim);

        // When
        ClaimResponse response = claimService.submitClaim(claimCreateRequest, 1L);

        // Then
        assertNotNull(response);
        assertEquals(ClaimStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("5000.00"), response.getClaimAmount());
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void testFailurePolicyNotFound() {
        // Given
        when(userPolicyRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> claimService.submitClaim(claimCreateRequest, 1L));
    }

    @Test
    void testFailureInactivePolicy() {
        // Given
        activeUserPolicy.setStatus(PolicyStatus.EXPIRED);
        when(userPolicyRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(activeUserPolicy));

        // When & Then
        assertThrows(BadRequestException.class,
                () -> claimService.submitClaim(claimCreateRequest, 1L));
    }

    @Test
    void testSuccessUpdateClaimStatus() {
        // Given
        ClaimStatusUpdateRequest updateRequest = ClaimStatusUpdateRequest.builder()
                .status(ClaimStatus.APPROVED)
                .reviewerComment("Claim approved after verification")
                .build();

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(pendingClaim));
        when(claimRepository.save(any(Claim.class)))
                .thenReturn(pendingClaim);

        // When
        ClaimResponse response = claimService.updateClaimStatus(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void testSuccessGetUserClaims() {
        // Given
        List<Claim> userClaims = Arrays.asList(pendingClaim);
        when(claimRepository.findByUserId(1L))
                .thenReturn(userClaims);

        // When
        List<ClaimResponse> responses = claimService.getUserClaims(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(ClaimStatus.PENDING, responses.get(0).getStatus());
    }
}
