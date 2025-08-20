package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.repository.dao.PolicyDao;
import com.innov8ors.insurance.repository.dao.UserDao;
import com.innov8ors.insurance.repository.dao.UserPolicyDao;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_ALREADY_HAS_POLICY;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_EMAIL;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.getPolicyPurchaseRequest;
import static com.innov8ors.insurance.util.TestUtil.getTestPolicy;
import static com.innov8ors.insurance.util.TestUtil.getTestUser;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicy;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicyPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserPolicyServiceImplTest {

    UserPolicyService userPolicyService;

    @Mock
    UserPolicyDao userPolicyDao;

    @Mock
    UserService userService;

    @Mock
    PolicyService policyService;

    @Mock
    UserDao userDao;

    @Mock
    PolicyDao policyDao;

    AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
        userPolicyService = new UserPolicyServiceImpl(userPolicyDao, userService, policyService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSuccessfulGetUserPolicies() {
        doReturn(getTestUser())
                .when(userService)
                .getByEmail(anyString());

        Page<UserPolicy> userPolicyPage = getUserPolicyPage();

        doReturn(userPolicyPage)
                .when(userPolicyDao)
                .findByUserIdWithPolicy(anyLong(), any(PageRequest.class));

        Page<UserPolicyResponse> userPolicies = userPolicyService.getUserPolicies(TEST_USER_EMAIL, 0, 10);

        assertEquals(userPolicyPage.getTotalElements(), userPolicies.getTotalElements());
        verify(userService).getByEmail(TEST_USER_EMAIL);
        verify(userPolicyDao).findByUserIdWithPolicy(eq(TEST_USER_ID), any(PageRequest.class));
        verifyNoMoreInteractions(userService, userPolicyDao);
    }

    @Test
    public void testSuccessfulPurchasePolicy() {
        doReturn(getTestUser())
                .when(userService)
                .getByEmail(anyString());

        doReturn(getTestPolicy())
                .when(policyService)
                .getById(anyLong());

        doReturn(false)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(anyLong(), anyLong());

        doReturn(getUserPolicy())
                .when(userPolicyDao)
                .save(any(UserPolicy.class));

        doReturn(Optional.of(getTestUser()))
                .when(userDao)
                .findById(anyLong());

        doReturn(Optional.of(getTestPolicy()))
                .when(policyDao)
                .findById(anyLong());

        UserPolicy userPolicy = userPolicyService.purchasePolicy(TEST_USER_EMAIL, getPolicyPurchaseRequest());

        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
        assertEquals(TEST_USER_ID, userPolicy.getUserId());
        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
        verify(userService).getByEmail(TEST_USER_EMAIL);
        verify(policyService).getById(TEST_POLICY_ID);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(userPolicyDao).save(any(UserPolicy.class));
        verify(userService).getById(TEST_USER_ID);
        verifyNoMoreInteractions(userService, policyService, userPolicyDao, userDao, policyDao);
    }

    @Test
    public void testFailurePurchasePolicy() {
        doReturn(getTestUser())
                .when(userService)
                .getByEmail(anyString());

        doReturn(getTestPolicy())
                .when(policyService)
                .getById(anyLong());

        doReturn(false)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(anyLong(), anyLong());

        doThrow(new RuntimeException("error"))
                .when(userPolicyDao)
                .save(any(UserPolicy.class));

        assertThrows(RuntimeException.class, () -> {
            userPolicyService.purchasePolicy(TEST_USER_EMAIL, getPolicyPurchaseRequest());
        });

        verify(userService).getByEmail(TEST_USER_EMAIL);
        verify(policyService).getById(TEST_POLICY_ID);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(userPolicyDao).save(any(UserPolicy.class));
        verifyNoMoreInteractions(userService, policyService, userPolicyDao, userDao, policyDao);
    }

    @Test
    public void testSuccessfulGetByUserIdAndPolicyId() {
        doReturn(Optional.of(getUserPolicy()))
                .when(userPolicyDao)
                .findByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        UserPolicy userPolicy = userPolicyService.getByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
        assertEquals(TEST_USER_ID, userPolicy.getUserId());
        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
        verify(userPolicyDao).findByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verifyNoMoreInteractions(userPolicyDao);
    }

    @Test
    public void testSuccessfulIsExistsByUserIdAndPolicyId() {
        doReturn(true)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        boolean exists = userPolicyService.isExistsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        assertTrue(exists);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verifyNoMoreInteractions(userPolicyDao);
    }

    @Test
    public void testFailureIsExistsByUserIdAndPolicyId() {
        doReturn(false)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        boolean exists = userPolicyService.isExistsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);

        assertFalse(exists);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verifyNoMoreInteractions(userPolicyDao);
    }

    @Test
    public void testSuccessfulPurchasePolicyWithUserId() {
        doReturn(getTestPolicy())
                .when(policyService)
                .getById(anyLong());

        doReturn(false)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(anyLong(), anyLong());

        doReturn(getUserPolicy())
                .when(userPolicyDao)
                .save(any(UserPolicy.class));

        doReturn(Optional.of(getTestUser()))
                .when(userDao)
                .findById(anyLong());

        doReturn(Optional.of(getTestPolicy()))
                .when(policyDao)
                .findById(anyLong());

        UserPolicy userPolicy = userPolicyService.purchasePolicy(TEST_USER_ID, getPolicyPurchaseRequest());

        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
        assertEquals(TEST_USER_ID, userPolicy.getUserId());
        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
        assertEquals(UserPolicyStatus.ACTIVE, userPolicy.getStatus());
        verify(policyService).getById(TEST_POLICY_ID);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(userPolicyDao).save(any(UserPolicy.class));
        verify(userService).getById(TEST_USER_ID);
        verifyNoMoreInteractions(policyService, userPolicyDao, userDao, policyDao);
    }

    @Test
    public void testFailurePurchasePolicyAlreadyExists() {
        doReturn(getTestPolicy())
                .when(policyService)
                .getById(anyLong());

        doReturn(true)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(anyLong(), anyLong());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userPolicyService.purchasePolicy(TEST_USER_ID, getPolicyPurchaseRequest());
        });

        assertEquals(USER_ALREADY_HAS_POLICY, exception.getMessage());
        verify(policyService).getById(TEST_POLICY_ID);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(userPolicyDao, never()).save(any(UserPolicy.class));
        verifyNoMoreInteractions(policyService, userPolicyDao);
    }

    @Test
    public void testPolicyExpiryStatusUpdate() {
        UserPolicy expiredPolicy1 = UserPolicy.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .policyId(TEST_POLICY_ID)
                .status(UserPolicyStatus.ACTIVE)
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        UserPolicy expiredPolicy2 = UserPolicy.builder()
                .id(2L)
                .userId(TEST_USER_ID + 1)
                .policyId(TEST_POLICY_ID + 1)
                .status(UserPolicyStatus.ACTIVE)
                .endDate(LocalDateTime.now().minusDays(5))
                .build();

        List<UserPolicy> expiredPolicies = Arrays.asList(expiredPolicy1, expiredPolicy2);

        doReturn(expiredPolicies)
                .when(userPolicyDao)
                .findByStatusAndEndDateBefore(eq(UserPolicyStatus.ACTIVE), any(LocalDateTime.class));

        doReturn(expiredPolicy1)
                .when(userPolicyDao)
                .save(expiredPolicy1);

        doReturn(expiredPolicy2)
                .when(userPolicyDao)
                .save(expiredPolicy2);

        try {
            Method updateExpiredPoliciesMethod = UserPolicyServiceImpl.class.getDeclaredMethod("updateExpiredPolicies");
            updateExpiredPoliciesMethod.setAccessible(true);
            updateExpiredPoliciesMethod.invoke(userPolicyService);
        } catch (Exception e) {
            fail("Failed to invoke updateExpiredPolicies method: " + e.getMessage());
        }

        assertEquals(UserPolicyStatus.EXPIRED, expiredPolicy1.getStatus());
        assertEquals(UserPolicyStatus.EXPIRED, expiredPolicy2.getStatus());

        verify(userPolicyDao).findByStatusAndEndDateBefore(eq(UserPolicyStatus.ACTIVE), any(LocalDateTime.class));
        verify(userPolicyDao, times(2)).save(any(UserPolicy.class));
        verifyNoMoreInteractions(userPolicyDao);
    }

    @Test
    public void testPolicyExpiryStatusUpdateNoExpiredPolicies() {
        doReturn(Collections.emptyList())
                .when(userPolicyDao)
                .findByStatusAndEndDateBefore(eq(UserPolicyStatus.ACTIVE), any(LocalDateTime.class));

        try {
            Method updateExpiredPoliciesMethod = UserPolicyServiceImpl.class.getDeclaredMethod("updateExpiredPolicies");
            updateExpiredPoliciesMethod.setAccessible(true);
            updateExpiredPoliciesMethod.invoke(userPolicyService);
        } catch (Exception e) {
            fail("Failed to invoke updateExpiredPolicies method: " + e.getMessage());
        }

        verify(userPolicyDao).findByStatusAndEndDateBefore(eq(UserPolicyStatus.ACTIVE), any(LocalDateTime.class));
        verify(userPolicyDao, never()).save(any(UserPolicy.class));
        verifyNoMoreInteractions(userPolicyDao);
    }

    @Test
    public void testMakePaymentNotImplemented() {
        UserPolicy result = userPolicyService.makePayment(TEST_USER_EMAIL, TEST_POLICY_ID);

        assertNull(result);
        verifyNoInteractions(userPolicyDao);
        verifyNoInteractions(userService);
        verifyNoInteractions(policyService);
    }

    @Test
    public void testPolicyPurchaseLogicValidation() {
        doReturn(getTestPolicy())
                .when(policyService)
                .getById(anyLong());

        doReturn(false)
                .when(userPolicyDao)
                .existsByUserIdAndPolicyId(anyLong(), anyLong());

        doReturn(getUserPolicy())
                .when(userPolicyDao)
                .save(any(UserPolicy.class));

        doReturn(Optional.of(getTestUser()))
                .when(userDao)
                .findById(anyLong());

        doReturn(Optional.of(getTestPolicy()))
                .when(policyDao)
                .findById(anyLong());

        UserPolicy userPolicy = userPolicyService.purchasePolicy(TEST_USER_ID, getPolicyPurchaseRequest());

        assertNotNull(userPolicy);
        assertEquals(TEST_USER_ID, userPolicy.getUserId());
        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
        assertEquals(UserPolicyStatus.ACTIVE, userPolicy.getStatus());
        assertNotNull(userPolicy.getStartDate());
        assertNotNull(userPolicy.getEndDate());

        Policy testPolicy = getTestPolicy();
        assertTrue(userPolicy.getEndDate().isAfter(userPolicy.getStartDate()));

        verify(policyService).getById(TEST_POLICY_ID);
        verify(userPolicyDao).existsByUserIdAndPolicyId(TEST_USER_ID, TEST_POLICY_ID);
        verify(userPolicyDao).save(any(UserPolicy.class));
        verify(userService).getById(TEST_USER_ID);
        verify(policyService).getById(TEST_POLICY_ID);
        verifyNoMoreInteractions(policyService, userPolicyDao, userDao, policyDao);
    }
}