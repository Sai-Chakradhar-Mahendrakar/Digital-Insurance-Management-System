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

    @Test
    void testFindByUserId() {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(42L);
        ticket.setSubject("Test subject");
        ticket.setDescription("Test description");
        ticket.setStatus(SupportTicketStatus.OPEN);
        ticket.setCreatedAt(java.time.LocalDateTime.now()); // Ensure createdAt is set
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
}
