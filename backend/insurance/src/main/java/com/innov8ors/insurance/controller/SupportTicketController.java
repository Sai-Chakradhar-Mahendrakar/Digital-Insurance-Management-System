package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public SupportTicket createTicket(@RequestBody SupportTicketCreateRequest request) {
        return supportTicketService.createTicket(request);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public List<SupportTicket> getTicketsByUser(@PathVariable Long userId) {
        return supportTicketService.getTicketsByUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketId}")
    public SupportTicket updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String response) {
        return supportTicketService.updateTicketStatus(ticketId, response, SupportTicketStatus.RESOLVED);
    }
}

