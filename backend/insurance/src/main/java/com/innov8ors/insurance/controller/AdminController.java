package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private final PolicyService policyService;
    private final SupportTicketService supportTicketService;
    private final UserPolicyService userPolicyService;

    public AdminController(PolicyService policyService, UserPolicyService userPolicyService, SupportTicketService supportTicketService) {
        this.policyService = policyService;
        this.userPolicyService = userPolicyService;
        this.supportTicketService = supportTicketService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public Policy addPolicy(@Valid @RequestBody PolicyCreateRequest policyCreateRequest) {
        log.debug("Received request to add policy: {}", policyCreateRequest);
        return policyService.addPolicy(policyCreateRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/policies/{policyId}/users")
    public Page<UserPolicyResponse> getUsersByPolicyId(
            @PathVariable Long policyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Fetching users for policy ID: {}", policyId);
        return userPolicyService.getUsersByPolicyId(policyId, page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/support/{ticketId}")
    public SupportTicket updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody SupportTicketUpdateRequest request) {
        log.info("Updating ticket status to RESOLVED for ticketId: {} with response: {}", ticketId, request.getResponse());
        SupportTicket ticket = supportTicketService.updateTicketStatus(ticketId, request.getResponse(), SupportTicketStatus.RESOLVED);
        log.info("Ticket {} status updated to RESOLVED", ticketId);
        return ticket;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/support/fetchAll")
    public List<SupportTicket> fetchAllTickets() {
        log.info("Fetching all support tickets (admin only)");
        List<SupportTicket> tickets = supportTicketService.fetchAllTickets();
        log.info("Found {} tickets in total", tickets.size());
        return tickets;
    }
}
