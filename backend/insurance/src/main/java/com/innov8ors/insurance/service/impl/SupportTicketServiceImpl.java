package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.mapper.SupportTicketMapper.fromCreateRequest;

@Service
@Slf4j
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public SupportTicket createTicket(SupportTicketCreateRequest request, Long userId) {
        try {
            log.info("Creating support ticket for userId: {} with subject: {}", userId, request.getSubject());
            SupportTicket ticket = fromCreateRequest(request, userId);
            ticket.setUserId(userId); // Set userId from authenticated principal
            SupportTicket savedTicket = supportTicketRepository.save(ticket);
            log.info("Support ticket created with id: {}", savedTicket.getId());
            return savedTicket;
        } catch (Exception e) {
            log.error("Error creating support ticket for userId: {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<SupportTicket> getTicketsByUser(Long userId) {
        List<SupportTicket> tickets = supportTicketRepository.findByUserId(userId);
        if (tickets == null) {
            return java.util.Collections.emptyList();
        }
        return tickets;
    }

    @Override
    public SupportTicket updateTicketStatus(Long ticketId, String response, SupportTicketStatus status) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setResponse(response);
            ticket.setStatus(status);
            if (status == SupportTicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            return supportTicketRepository.save(ticket);
        }
        throw new NotFoundException("Support ticket not found ticketId: " + ticketId);
    }

    @Override
    public List<SupportTicket> fetchAllTickets() {
        return supportTicketRepository.findAll();
    }
}
