package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.innov8ors.insurance.entity.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/support")
@CrossOrigin(origins = "*")
@Slf4j
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public SupportTicket createTicket(
            @Valid @RequestBody SupportTicketCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("Creating support ticket for userId: {} with subject: {}", userPrincipal.getId(), request.getSubject());
        SupportTicket ticket = supportTicketService.createTicket(request, userPrincipal.getId());
        log.info("Support ticket created with id: {}", ticket.getId());
        return ticket;
    }

    @GetMapping("/user/{userId}")
    public List<SupportTicket> getTicketsByUser(@PathVariable Long userId) {
        log.info("Fetching support tickets for userId: {}", userId);
        List<SupportTicket> tickets = supportTicketService.getTicketsByUser(userId);
        log.info("Found {} tickets for userId: {}", tickets.size(), userId);
        return tickets;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/getTicketsByUser")
    public List<SupportTicket> getTicketsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("Fetching support tickets for userId: {}", userPrincipal.getId());
        List<SupportTicket> tickets = supportTicketService.getTicketsByUser(userPrincipal.getId());
        log.info("Found {} tickets for userId: {}", tickets.size(), userPrincipal.getId());
        return tickets;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{ticketId}")
    public SupportTicket updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody SupportTicketUpdateRequest request) {
        log.info("Updating ticket status to RESOLVED for ticketId: {} with response: {}", ticketId, request.getResponse());
        SupportTicket ticket = supportTicketService.updateTicketStatus(ticketId, request.getResponse(), SupportTicketStatus.RESOLVED);
        log.info("Ticket {} status updated to RESOLVED", ticketId);
        return ticket;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/fetchAll")
    public List<SupportTicket> fetchAllTickets() {
        log.info("Fetching all support tickets (admin only)");
        List<SupportTicket> tickets = supportTicketService.fetchAllTickets();
        log.info("Found {} tickets in total", tickets.size());
        return tickets;
    }
}
