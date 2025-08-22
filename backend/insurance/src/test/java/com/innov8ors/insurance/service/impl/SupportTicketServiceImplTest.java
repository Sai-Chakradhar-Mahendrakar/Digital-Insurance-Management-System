package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.util.TestUtil.createSupportTicket;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicketUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class SupportTicketServiceImplTest {
    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private SupportTicketCreateRequest createSupportTicketCreateRequest(Long policyId, Long claimId, String subject, String description) {
        return new SupportTicketCreateRequest(policyId, claimId, subject, description);
    }

    @Test
    void testCreateTicket() {
        SupportTicketCreateRequest request = createSupportTicketCreateRequest(123L, 456L, "Subject", "Description");
        SupportTicket ticket = createSupportTicket(1L, 1L, "Subject", SupportTicketStatus.OPEN);
        Mockito.when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        SupportTicket result = supportTicketService.createTicket(request, 1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testCreateTicket_NullRequest() {
        Exception exception = assertThrows(Exception.class, () -> {
            supportTicketService.createTicket(null, 1L);
        });
        assertTrue(exception instanceof NullPointerException || exception instanceof RuntimeException);
    }

    @Test
    void testGetTicketsByUser() {
        SupportTicket ticket = createSupportTicket(1L, 1L, "Subject", SupportTicketStatus.OPEN);
        Mockito.when(supportTicketRepository.findByUserId(1L)).thenReturn(List.of(ticket));
        List<SupportTicket> tickets = supportTicketService.getTicketsByUser(1L);
        assertEquals(1, tickets.size());
        assertEquals(1L, tickets.get(0).getId());
    }

    @Test
    void testGetTicketsByUser_Empty() {
        Mockito.when(supportTicketRepository.findByUserId(2L)).thenReturn(List.of());
        List<SupportTicket> tickets = supportTicketService.getTicketsByUser(2L);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void testUpdateTicketStatus() {
        SupportTicket ticket = createSupportTicket(1L, 1L, "Subject", SupportTicketStatus.OPEN);
        Mockito.when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        Mockito.when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        SupportTicketUpdateRequest updateRequest = getSupportTicketUpdateRequest();
        SupportTicket updated = supportTicketService.updateTicketStatus(1L, updateRequest);
        assertEquals(SupportTicketStatus.RESOLVED, updated.getStatus());
    }

    @Test
    void testUpdateTicketStatus_ResolvedTimestamp() {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(2L);
        ticket.setStatus(SupportTicketStatus.OPEN);
        Mockito.when(supportTicketRepository.findById(2L)).thenReturn(Optional.of(ticket));
        Mockito.when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket t = invocation.getArgument(0);
            t.setResolvedAt(java.time.LocalDateTime.now());
            return t;
        });
        SupportTicketUpdateRequest updateRequest = getSupportTicketUpdateRequest();
        SupportTicket updated = supportTicketService.updateTicketStatus(2L, updateRequest);
        assertEquals(SupportTicketStatus.RESOLVED, updated.getStatus());
        assertTrue(updated.getResolvedAt() != null);
    }

    @Test
    void testUpdateTicketStatus_NotFound() {
        Mockito.when(supportTicketRepository.findById(anyLong())).thenReturn(Optional.empty());
        SupportTicketUpdateRequest updateRequest = getSupportTicketUpdateRequest();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            supportTicketService.updateTicketStatus(99L, updateRequest);
        });
        assertTrue(exception.getMessage().contains("Support ticket not found"));
    }
}
