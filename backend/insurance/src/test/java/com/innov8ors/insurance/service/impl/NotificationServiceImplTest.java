package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.dao.NotificationDao;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.NOTIFICATION_NOT_FOUND_OR_DOES_NOT_BELONG_TO_USER;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_NOT_FOUND;
import static com.innov8ors.insurance.util.TestUtil.TEST_NOTIFICATION_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_NOTIFICATION_MESSAGE;
import static com.innov8ors.insurance.util.TestUtil.TEST_NOTIFICATION_STATUS;
import static com.innov8ors.insurance.util.TestUtil.TEST_NOTIFICATION_TYPE;
import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.getNotification;
import static com.innov8ors.insurance.util.TestUtil.getNotificationByPolicyRequest;
import static com.innov8ors.insurance.util.TestUtil.getNotificationSendBulkRequest;
import static com.innov8ors.insurance.util.TestUtil.getNotificationSendRequest;
import static com.innov8ors.insurance.util.TestUtil.getNotifications;
import static com.innov8ors.insurance.util.TestUtil.getUser;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicy;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicyPaginatedResponse;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

public class NotificationServiceImplTest {
    @Mock
    private NotificationDao notificationDao;

    @Mock
    private UserService userService;

    @Mock
    private UserPolicyService userPolicyService;

    private NotificationService notificationService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
        notificationService = new NotificationServiceImpl(notificationDao, userService, userPolicyService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSuccessfulMarkNotificationAsRead() {
        doReturn(Optional.of(getNotification()))
                .when(notificationDao)
                .getByIdAndUserId(any(), any());
        doReturn(getNotification())
                .when(notificationDao)
                .persist(any());

        notificationService.markNotificationAsRead(TEST_NOTIFICATION_ID, TEST_USER_ID);

        verify(notificationDao).getByIdAndUserId(TEST_NOTIFICATION_ID, TEST_USER_ID);
        verify(notificationDao).persist(any(Notification.class));
        verifyNoMoreInteractions(notificationDao);
    }

    @Test
    public void testFailureMarkNotificationAsReadDueToNotFound() {
        doReturn(Optional.empty())
                .when(notificationDao)
                .getByIdAndUserId(any(), any());

        try {
            notificationService.markNotificationAsRead(TEST_NOTIFICATION_ID, TEST_USER_ID);
            fail("Expected NotFoundException was not thrown");
        } catch (Exception e) {
            assertInstanceOf(NotFoundException.class, e);
            assertEquals(NOTIFICATION_NOT_FOUND_OR_DOES_NOT_BELONG_TO_USER, e.getMessage());
            verify(notificationDao).getByIdAndUserId(TEST_NOTIFICATION_ID, TEST_USER_ID);
            verifyNoMoreInteractions(notificationDao);
        }
    }

    @Test
    public void testSuccessfulSendNotification() {
        doReturn(getUser())
                .when(userService)
                .getById(TEST_USER_ID);
        doReturn(getNotification())
                .when(notificationDao)
                .persist(any());

        NotificationResponse response = notificationService.sendNotification(getNotificationSendRequest());

        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(TEST_NOTIFICATION_MESSAGE, response.getMessage());
        assertEquals(TEST_NOTIFICATION_TYPE, response.getType());
        assertEquals(TEST_NOTIFICATION_STATUS, response.getStatus());
        verify(userService).getById(TEST_USER_ID);
        verify(notificationDao).persist(any(Notification.class));
        verifyNoMoreInteractions(notificationDao, userService);
    }

    @Test
    public void testFailureSendNotificationDueToUserNotFound() {
        doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService)
                .getById(TEST_USER_ID);

        try {
            notificationService.sendNotification(getNotificationSendRequest());
            fail("Expected NotFoundException was not thrown");
        } catch (Exception e) {
            assertInstanceOf(NotFoundException.class, e);
            assertEquals(USER_NOT_FOUND, e.getMessage());
            verify(userService).getById(TEST_USER_ID);
            verifyNoMoreInteractions(notificationDao, userService);
        }
    }

    @Test
    public void testSuccessfulSendNotificationsBulk() {
        doReturn(getUser())
                .when(userService)
                .getById(TEST_USER_ID);
        doReturn(getNotification())
                .when(notificationDao)
                .persist(any());

        notificationService.sendNotificationsBulk(getNotificationSendBulkRequest());

        verify(userService).getById(TEST_USER_ID);
        verify(notificationDao).persist(any(Notification.class));
        verifyNoMoreInteractions(notificationDao, userService);
    }

    @Test
    public void testSuccessfulMarkAllNotificationsAsRead() {
        doReturn(getNotifications())
                .when(notificationDao)
                .findByUserIdAndStatus(any(), any());
        doReturn(null)
                .when(notificationDao)
                .saveAll(any());

        notificationService.markAllNotificationsAsRead(TEST_USER_ID);

        verify(notificationDao).findByUserIdAndStatus(TEST_USER_ID, TEST_NOTIFICATION_STATUS);
        verify(notificationDao).saveAll(anyList());
        verifyNoMoreInteractions(notificationDao);
    }

    @Test
    public void testSuccessfulSendNotificationByPolicy() {
        doReturn(getUser())
                .when(userService)
                .getById(TEST_USER_ID);
        doReturn(getNotification())
                .when(notificationDao)
                .persist(any());
        doReturn(getUserPolicyPaginatedResponse())
                .when(userPolicyService)
                .getUsersByPolicyId(any(), any(), any());

        notificationService.sendNotificationByPolicy(getNotificationByPolicyRequest());

        verify(userService).getById(TEST_USER_ID);
        verify(notificationDao).persist(any(Notification.class));
        verify(userPolicyService).getUsersByPolicyId(TEST_POLICY_ID, 0, Integer.MAX_VALUE);
        verifyNoMoreInteractions(notificationDao, userService);
    }
}
