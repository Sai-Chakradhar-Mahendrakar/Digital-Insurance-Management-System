package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.repository.NotificationRepository;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {
        NotificationSendRequest request = new NotificationSendRequest();
        request.setUserId(1L);
        request.setTitle("Test Title");
        request.setMessage("Test Message");

        notificationService.sendNotification(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals(request.getUserId(), saved.getUserId());
        assertEquals(request.getTitle(), saved.getTitle());
        assertEquals(request.getMessage(), saved.getMessage());
        assertEquals(NotificationStatus.UNREAD, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testGetNotificationsForUser() {
        Notification n1 = new Notification(1L, "Title1", "Msg1", NotificationStatus.UNREAD, LocalDateTime.now());
        n1.setId(10L);
        Notification n2 = new Notification(1L, "Title2", "Msg2", NotificationStatus.READ, LocalDateTime.now());
        n2.setId(11L);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(n1, n2));

        List<NotificationResponse> responses = notificationService.getNotificationsForUser(1L);
        assertEquals(2, responses.size());
        assertEquals("Title1", responses.get(0).getTitle());
        assertEquals(NotificationStatus.UNREAD, responses.get(0).getStatus());
        assertEquals("Title2", responses.get(1).getTitle());
        assertEquals(NotificationStatus.READ, responses.get(1).getStatus());
    }

    @Test
    void testMarkAsRead() {
        Notification n = new Notification(1L, "Title", "Msg", NotificationStatus.UNREAD, LocalDateTime.now());
        n.setId(100L);
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(100L);
        assertEquals(NotificationStatus.READ, n.getStatus());
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(999L));
    }
}

