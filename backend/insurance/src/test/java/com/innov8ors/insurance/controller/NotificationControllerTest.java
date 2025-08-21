package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.response.NotificationPaginatedResponse;
import com.innov8ors.insurance.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static com.innov8ors.insurance.util.TestUtil.TEST_NOTIFICATION_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.getNotificationPaginatedResponse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class NotificationControllerTest {
    @Mock
    private NotificationService notificationService;

    private NotificationController notificationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        notificationController = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserPrincipal mockPrincipal = Mockito.mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(TEST_USER_ID);

        authentication = new UsernamePasswordAuthenticationToken(
                mockPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testSuccessfulGetNotifications() throws Exception {
        NotificationPaginatedResponse mockResponse = getNotificationPaginatedResponse();
        when(notificationService.getNotificationsByUserId(TEST_USER_ID, null, null, 0, 10))
                .thenReturn(mockResponse);

        this.mockMvc.perform(get("/notifications/user")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(notificationService).getNotificationsByUserId(TEST_USER_ID, null, null, 0, 10);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    public void testSuccessfulMarkNotificationAsRead() throws Exception {
       doNothing()
               .when(notificationService)
               .markNotificationAsRead(TEST_NOTIFICATION_ID, TEST_USER_ID);

        this.mockMvc.perform(put("/notifications/{notificationId}/read", TEST_NOTIFICATION_ID)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(notificationService).markNotificationAsRead(TEST_NOTIFICATION_ID, TEST_USER_ID);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    public void testSuccessfulMarkAllNotificationsAsRead() throws Exception {
        doNothing()
                .when(notificationService)
                .markAllNotificationsAsRead(TEST_USER_ID);

        this.mockMvc.perform(put("/notifications/read-all")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(notificationService).markAllNotificationsAsRead(TEST_USER_ID);
        verifyNoMoreInteractions(notificationService);
    }
}
