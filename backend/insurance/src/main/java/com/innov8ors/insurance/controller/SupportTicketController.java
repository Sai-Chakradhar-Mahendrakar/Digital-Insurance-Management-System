package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketAdminResponseRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/support")
@CrossOrigin(origins = "*")
@Slf4j
public class SupportTicketController {
    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    // POST /support – Submit a query related to a policy or claim
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public SupportTicket submitTicket(@RequestBody SupportTicketCreateRequest request) {
        log.debug("Received support ticket submission: {}", request);
        return supportTicketService.createTicket(request);
    }

    // GET /support/user/{userId} – View submitted tickets
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")
    public List<SupportTicket> getTicketsByUser(@PathVariable Long userId) {
        return supportTicketService.getTicketsByUserId(userId);
    }

    // PUT /support/{ticketId} – Admin response and status change
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketId}")
    public SupportTicket respondToTicket(@PathVariable Long ticketId,
                                         @RequestBody SupportTicketAdminResponseRequest request) {
        log.debug("Admin responding to ticket {}: {}", ticketId, request);
        return supportTicketService.respondToTicket(ticketId, request);
    }
}
