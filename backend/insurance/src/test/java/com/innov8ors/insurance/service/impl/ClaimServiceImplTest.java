package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.exception.BadRequestException;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.dao.ClaimDao;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.UserPolicyUpdateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIMS_ALLOWED_ONLY_FOR_ACTIVE_POLICIES;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIM_AMOUNT_EXCEEDS_POLICY_COVERAGE;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.CLAIM_NOT_FOUND;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_AMOUNT;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_DATE;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_REASON;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_REVIEWER_COMMENT;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_STATUS;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_COVERAGE_AMOUNT;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.getClaim;
import static com.innov8ors.insurance.util.TestUtil.getClaimCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getClaimStatusUpdateRequest;
import static com.innov8ors.insurance.util.TestUtil.getClaimsPage;
import static com.innov8ors.insurance.util.TestUtil.getPolicy;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {
    @Mock
    private ClaimDao claimDao;

    @Mock
    private UserPolicyService userPolicyService;

    @Mock
    private PolicyService policyService;

    @Mock
    private NotificationService notificationService;

    private ClaimService claimService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        claimService = new ClaimServiceImpl(claimDao, userPolicyService, policyService, notificationService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testSuccessfulSubmitClaim() {
        doReturn(getUserPolicy())
                .when(userPolicyService)
                .getByUserIdAndPolicyId(any(), any());
        doReturn(getPolicy())
                .when(policyService)
                .getById(any());
        doReturn(getClaim())
                .when(claimDao)
                .persist(any());

        ClaimResponse claimResponse = claimService.submitClaim(getClaimCreateRequest(), TEST_USER_ID);

        assertNotNull(claimResponse);
        assertEquals(TEST_USER_POLICY_ID, claimResponse.getUserPolicyId());
        assertEquals(ClaimStatus.PENDING, claimResponse.getStatus());
        assertEquals(TEST_CLAIM_AMOUNT, claimResponse.getClaimAmount());
        assertEquals(TEST_CLAIM_DATE, claimResponse.getClaimDate());
        assertEquals(TEST_CLAIM_REASON, claimResponse.getReason());
        verify(userPolicyService).getByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(policyService).getById(TEST_POLICY_ID);
        verify(claimDao).persist(any(Claim.class));
        verifyNoMoreInteractions(userPolicyService, policyService, claimDao);
    }

    @Test
    void testFailureSubmitClaimDueToExceedsCoverage() {
        ClaimCreateRequest claimCreateRequest = getClaimCreateRequest();
        claimCreateRequest.setClaimAmount(TEST_POLICY_COVERAGE_AMOUNT.add(BigDecimal.ONE)); // Exceeding coverage

        doReturn(getUserPolicy())
                .when(userPolicyService)
                .getByUserIdAndPolicyId(any(), any());
        doReturn(getPolicy())
                .when(policyService)
                .getById(any());

        try {
            claimService.submitClaim(claimCreateRequest, TEST_USER_ID);
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertInstanceOf(BadRequestException.class, e);
            assertEquals(CLAIM_AMOUNT_EXCEEDS_POLICY_COVERAGE, e.getMessage());
            verify(userPolicyService).getByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
            verify(policyService).getById(TEST_POLICY_ID);
            verifyNoMoreInteractions(userPolicyService, policyService, claimDao);
        }
    }

    @Test
    void testFailureInactivePolicy() {
        UserPolicy userPolicy = getUserPolicy();
        userPolicy.setStatus(UserPolicyStatus.EXPIRED);
        doReturn(userPolicy)
                .when(userPolicyService)
                .getByUserIdAndPolicyId(any(), any());
        doReturn(getPolicy())
                .when(policyService)
                .getById(any());

        try {
            claimService.submitClaim(getClaimCreateRequest(), TEST_USER_ID);
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertInstanceOf(BadRequestException.class, e);
            assertEquals(CLAIMS_ALLOWED_ONLY_FOR_ACTIVE_POLICIES, e.getMessage());
            verify(userPolicyService).getByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
            verify(policyService).getById(TEST_POLICY_ID);
            verifyNoMoreInteractions(userPolicyService, policyService, claimDao);
        }
    }

    @Test
    void testSuccessUpdateClaimStatus() {
        doReturn(Optional.of(getClaim()))
                .when(claimDao)
                .findById(TEST_CLAIM_ID);
        doReturn(getClaim())
                .when(claimDao)
                .persist(any(Claim.class));
        doReturn(getUserPolicy())
                .when(userPolicyService)
                .getById(any());
        doReturn(getPolicy())
                .when(policyService)
                .getById(any());
        doReturn(List.of(getClaim()))
                .when(claimDao)
                .getByUserPolicyId(any());

        ClaimResponse claimResponse = claimService.updateClaimStatus(TEST_USER_ID, TEST_CLAIM_ID, getClaimStatusUpdateRequest());

        assertNotNull(claimResponse);
        assertEquals(TEST_CLAIM_ID, claimResponse.getId());
        assertEquals(TEST_CLAIM_STATUS, claimResponse.getStatus());
        assertEquals(TEST_CLAIM_REVIEWER_COMMENT, claimResponse.getReviewerComment());
        verify(claimDao).findById(TEST_CLAIM_ID);
        verify(claimDao).persist(any(Claim.class));
        verify(userPolicyService).getById(TEST_USER_POLICY_ID);
        verify(policyService).getById(TEST_POLICY_ID);
        verify(claimDao).getByUserPolicyId(TEST_USER_POLICY_ID);
        verifyNoMoreInteractions(claimDao, userPolicyService, policyService);
    }

    @Test
    void testFailureUpdateClaimStatusDueToNonExistentClaim() {
        doReturn(Optional.empty())
                .when(claimDao)
                .findById(TEST_CLAIM_ID);

        try {
            claimService.updateClaimStatus(TEST_USER_ID, TEST_CLAIM_ID, getClaimStatusUpdateRequest());
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertInstanceOf(NotFoundException.class, e);
            assertEquals(CLAIM_NOT_FOUND, e.getMessage());
            verify(claimDao).findById(TEST_CLAIM_ID);
            verifyNoMoreInteractions(claimDao);
        }
    }

    @Test
    void testSuccessfulGetByClaimIdAndUserId() {
        doReturn(Optional.of(getClaim()))
                .when(claimDao)
                .getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);

        ClaimResponse claimResponse = claimService.getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);

        assertNotNull(claimResponse);
        assertEquals(TEST_CLAIM_ID, claimResponse.getId());
        verify(claimDao).getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);
        verifyNoMoreInteractions(claimDao);
    }

    @Test
    void testFailureGetByClaimIdAndUserIdDueToNonExistentClaim() {
        doReturn(Optional.empty())
                .when(claimDao)
                .getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);

        try {
            claimService.getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertInstanceOf(NotFoundException.class, e);
            assertEquals(CLAIM_NOT_FOUND, e.getMessage());
            verify(claimDao).getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);
            verifyNoMoreInteractions(claimDao);
        }
    }

    @Test
    void testSuccessfulGetAllClaims() {
        doReturn(getClaimsPage())
                .when(claimDao)
                .findAll(any(Specification.class), any(Pageable.class));

        ClaimPaginatedResponse response = claimService.getAllClaims(null, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getClaims().size());
        assertEquals(TEST_CLAIM_ID, response.getClaims().get(0).getId());
        verify(claimDao).findAll(any(Specification.class), any(Pageable.class));
    }
}
