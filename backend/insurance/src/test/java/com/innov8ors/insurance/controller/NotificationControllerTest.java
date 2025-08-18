package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.innov8ors.insurance.config.JwtFilter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)})
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationResponse notification1;
    private NotificationResponse notification2;

    @BeforeEach
    void setUp() {
        notification1 = new NotificationResponse();
        notification1.setId(1L);
        notification1.setTitle("Renewal Reminder");
        notification1.setMessage("Your policy will expire in 30 days.");
        notification1.setStatus(NotificationStatus.UNREAD);
        notification1.setCreatedAt(LocalDateTime.now());

        notification2 = new NotificationResponse();
        notification2.setId(2L);
        notification2.setTitle("Claim Update");
        notification2.setMessage("Your claim has been approved.");
        notification2.setStatus(NotificationStatus.READ);
        notification2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetNotifications() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.getNotificationsForUser(1L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Renewal Reminder"))
                .andExpect(jsonPath("$[1].title").value("Claim Update"));
    }

    @Test
    void testSendNotification() throws Exception {
        NotificationSendRequest request = new NotificationSendRequest();
        request.setUserId(1L);
        request.setTitle("Support Response");
        request.setMessage("Your support ticket has been resolved.");
        doNothing().when(notificationService).sendNotification(any(NotificationSendRequest.class));

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testMarkAsRead() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);
        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk());
    }
}
