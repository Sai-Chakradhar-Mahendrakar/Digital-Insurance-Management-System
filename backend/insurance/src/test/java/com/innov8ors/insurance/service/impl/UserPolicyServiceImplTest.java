//package com.innov8ors.insurance.service.impl;
//
//import com.innov8ors.insurance.entity.UserPolicy;
//import com.innov8ors.insurance.exception.NotFoundException;
//import com.innov8ors.insurance.repository.dao.UserPolicyDao;
//import com.innov8ors.insurance.service.UserPolicyService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.data.domain.Page;
//
//import java.util.Optional;
//
//import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_POLICY_NOT_FOUND;
//import static com.innov8ors.insurance.util.TestUtil.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//import static org.mockito.MockitoAnnotations.openMocks;
//
//public class UserPolicyServiceImplTest {
//
//    UserPolicyService userPolicyService;
//
//    @Mock
//    UserPolicyDao userPolicyDao;
//
//    AutoCloseable closeable;
//
//    @BeforeEach
//    public void setUp() {
//        closeable = openMocks(this);
//        userPolicyService = new UserPolicyServiceImpl(userPolicyDao);
//    }
//
//    @AfterEach
//    public void tearDown() throws Exception {
//        closeable.close();
//    }
//
//    @Test
//    public void testSuccessfulGetUserPolicies() {
//        doReturn(getUserPoliciesPage())
//                .when(userPolicyDao)
//                .getByUserId(anyLong(), any(), any());
//
//        Page<UserPolicy> userPolicies = userPolicyService.getUserPolicies(TEST_USER_ID, null, 0, 10);
//
//        assertEquals(5, userPolicies.getTotalElements());
//        verify(userPolicyDao).getByUserId(eq(TEST_USER_ID), any(), any());
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testSuccessfulCreateUserPolicy() {
//        doReturn(getUserPolicy())
//                .when(userPolicyDao)
//                .persist(any(UserPolicy.class));
//
//        UserPolicy userPolicy = userPolicyService.createUserPolicy(getUserPolicyCreateRequest());
//
//        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
//        assertEquals(TEST_USER_ID, userPolicy.getUserId());
//        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
//        assertEquals(TEST_USER_POLICY_STATUS, userPolicy.getStatus());
//        verify(userPolicyDao).persist(any(UserPolicy.class));
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testFailureCreateUserPolicy() {
//        doThrow(new RuntimeException("error"))
//                .when(userPolicyDao)
//                .persist(any(UserPolicy.class));
//
//        try {
//            userPolicyService.createUserPolicy(getUserPolicyCreateRequest());
//            fail("Expected an exception to be thrown");
//        } catch (Exception ignored) {
//            assertEquals("error", ignored.getMessage());
//        }
//        verify(userPolicyDao).persist(any(UserPolicy.class));
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testSuccessfulGetUserPolicyById() {
//        doReturn(Optional.of(getUserPolicy()))
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//
//        UserPolicy userPolicy = userPolicyService.getUserPolicyById(TEST_USER_POLICY_ID);
//
//        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
//        assertEquals(TEST_USER_ID, userPolicy.getUserId());
//        assertEquals(TEST_POLICY_ID, userPolicy.getPolicyId());
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testFailureGetUserPolicyById() {
//        doReturn(Optional.empty())
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//
//        try {
//            userPolicyService.getUserPolicyById(TEST_USER_POLICY_ID);
//            fail("Expected NotFoundException to be thrown");
//        } catch (Exception exception) {
//            assertInstanceOf(NotFoundException.class, exception);
//            assertEquals(USER_POLICY_NOT_FOUND, exception.getMessage());
//        }
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testSuccessfulUpdateUserPolicyStatus() {
//        doReturn(Optional.of(getUserPolicy()))
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//        doReturn(getUserPolicyWithUpdatedStatus())
//                .when(userPolicyDao)
//                .update(any(UserPolicy.class));
//
//        UserPolicy userPolicy = userPolicyService.updateUserPolicyStatus(TEST_USER_POLICY_ID, getUserPolicyStatusUpdateRequest());
//
//        assertEquals(TEST_USER_POLICY_ID, userPolicy.getId());
//        assertEquals(TEST_UPDATED_STATUS, userPolicy.getStatus());
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verify(userPolicyDao).update(any(UserPolicy.class));
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testFailureUpdateUserPolicyStatusNotFound() {
//        doReturn(Optional.empty())
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//
//        try {
//            userPolicyService.updateUserPolicyStatus(TEST_USER_POLICY_ID, getUserPolicyStatusUpdateRequest());
//            fail("Expected NotFoundException to be thrown");
//        } catch (Exception exception) {
//            assertInstanceOf(NotFoundException.class, exception);
//            assertEquals(USER_POLICY_NOT_FOUND, exception.getMessage());
//        }
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testSuccessfulDeleteUserPolicy() {
//        doReturn(Optional.of(getUserPolicy()))
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//        doNothing()
//                .when(userPolicyDao)
//                .delete(TEST_USER_POLICY_ID);
//
//        assertTrue(userPolicyService.deleteUserPolicy(TEST_USER_POLICY_ID));
//
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verify(userPolicyDao).delete(TEST_USER_POLICY_ID);
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//
//    @Test
//    public void testFailureDeleteUserPolicyNotFound() {
//        doReturn(Optional.empty())
//                .when(userPolicyDao)
//                .getById(TEST_USER_POLICY_ID);
//
//        try {
//            userPolicyService.deleteUserPolicy(TEST_USER_POLICY_ID);
//            fail("Expected NotFoundException to be thrown");
//        } catch (Exception exception) {
//            assertInstanceOf(NotFoundException.class, exception);
//            assertEquals(USER_POLICY_NOT_FOUND, exception.getMessage());
//        }
//        verify(userPolicyDao).getById(TEST_USER_POLICY_ID);
//        verifyNoMoreInteractions(userPolicyDao);
//    }
//}