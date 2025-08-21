package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class SupportTicketRepositoryTest {
    @Autowired
    private SupportTicketRepository supportTicketRepository;

    private SupportTicket createSupportTicket(Long userId, String subject, String description, SupportTicketStatus status) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(userId);
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setStatus(status);
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        return ticket;
    }

    @Test
    void testFindByUserId() {
        SupportTicket ticket = createSupportTicket(42L, "Test subject", "Test description", SupportTicketStatus.OPEN);
        supportTicketRepository.save(ticket);

        List<SupportTicket> tickets = supportTicketRepository.findByUserId(42L);
        assertFalse(tickets.isEmpty());
        assertEquals(42L, tickets.get(0).getUserId());
    }

    @Test
    void testFindByUserId_Empty() {
        List<SupportTicket> tickets = supportTicketRepository.findByUserId(999L);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void testSaveAndDeleteTicket() {
        SupportTicket ticket = createSupportTicket(100L, "Delete subject", "Delete description", SupportTicketStatus.OPEN);
        SupportTicket saved = supportTicketRepository.save(ticket);
        assertEquals(100L, saved.getUserId());
        supportTicketRepository.delete(saved);
        List<SupportTicket> tickets = supportTicketRepository.findByUserId(100L);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void testSaveTicketWithNullFields() {
        SupportTicket ticket = createSupportTicket(101L, null, null, SupportTicketStatus.OPEN);
        try {
            supportTicketRepository.save(ticket);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }
    }
}
