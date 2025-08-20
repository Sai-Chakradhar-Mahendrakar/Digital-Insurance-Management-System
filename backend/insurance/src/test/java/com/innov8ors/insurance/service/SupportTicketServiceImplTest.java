package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class SupportTicketServiceImplTest {
    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTicket() {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest(123L, 456L, "Subject", "Description");
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        Mockito.when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        SupportTicket result = supportTicketService.createTicket(request, 1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetTicketsByUser() {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        Mockito.when(supportTicketRepository.findByUserId(1L)).thenReturn(List.of(ticket));
        List<SupportTicket> tickets = supportTicketService.getTicketsByUser(1L);
        assertEquals(1, tickets.size());
        assertEquals(1L, tickets.get(0).getId());
    }

    @Test
    void testUpdateTicketStatus() {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        ticket.setStatus(SupportTicketStatus.OPEN);
        Mockito.when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        Mockito.when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        SupportTicket updated = supportTicketService.updateTicketStatus(1L, "Resolved", SupportTicketStatus.RESOLVED);
        assertEquals(SupportTicketStatus.RESOLVED, updated.getStatus());
    }

    @Test
    void testUpdateTicketStatus_NotFound() {
        Mockito.when(supportTicketRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            supportTicketService.updateTicketStatus(99L, "Response", SupportTicketStatus.RESOLVED);
        });
        assertTrue(exception.getMessage().contains("Support ticket not found"));
    }
}

