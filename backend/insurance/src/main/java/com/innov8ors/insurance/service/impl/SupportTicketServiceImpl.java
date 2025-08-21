package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.repository.SupportTicketRepository;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.mapper.NotificationMapper;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.mapper.SupportTicketMapper.fromCreateRequest;

@Service
@Slf4j
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final NotificationService notificationService;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, NotificationService notificationService) {
        this.supportTicketRepository = supportTicketRepository;
        this.notificationService = notificationService;
    }

    @Override
    public SupportTicket createTicket(SupportTicketCreateRequest request, Long userId) {
        try {
            log.info("Creating support ticket for userId: {} with subject: {}", userId, request.getSubject());
            SupportTicket ticket = fromCreateRequest(request, userId);
            ticket.setUserId(userId); // Set userId from authenticated principal
            SupportTicket savedTicket = supportTicketRepository.save(ticket);
            log.info("Support ticket created with id: {}", savedTicket.getId());
            notificationService.sendNotification(userId, "Your support ticket has been created successfully. Ticket ID: " + savedTicket.getId(), NotificationType.SUPPORT_RESPONSE);
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
    public SupportTicket updateTicketStatus(Long ticketId, SupportTicketUpdateRequest request) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setResponse(request.getResponse());
            ticket.setStatus(request.getStatus());
            if (request.getStatus() == SupportTicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            SupportTicket updatedTicket = supportTicketRepository.save(ticket);
            log.info("Support ticket updated with id: {}", updatedTicket.getId());
            notificationService.sendNotification(updatedTicket.getUserId(),
                    "Your support ticket has been updated. Ticket ID: " + updatedTicket.getId() + ", Status: " + updatedTicket.getStatus(),
                    NotificationType.SUPPORT_RESPONSE);
            return updatedTicket;
        }
        throw new NotFoundException("Support ticket not found ticketId: " + ticketId);
    }

    @Override
    public List<SupportTicket> fetchAllTickets() {
        return supportTicketRepository.findAll();
    }
}
