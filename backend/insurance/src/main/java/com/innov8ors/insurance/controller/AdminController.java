package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.request.NotificationByPolicyRequest;
import com.innov8ors.insurance.request.NotificationSendBulkRequest;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.response.UserPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.service.ClaimService;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.SupportTicketService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private final PolicyService policyService;
    private final SupportTicketService supportTicketService;
    private final UserPolicyService userPolicyService;
    private final ClaimService claimService;
    private final NotificationService notificationService;
    private final UserService userService;

    public AdminController(
            PolicyService policyService,
            UserPolicyService userPolicyService,
            SupportTicketService supportTicketService,
            ClaimService claimService,
            NotificationService notificationService,
            UserService userService)
    {
        this.policyService = policyService;
        this.userPolicyService = userPolicyService;
        this.supportTicketService = supportTicketService;
        this.claimService = claimService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public Policy addPolicy(@Valid @RequestBody PolicyCreateRequest policyCreateRequest) {
        log.debug("Received request to add policy: {}", policyCreateRequest);
        return policyService.addPolicy(policyCreateRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/policies/{policyId}/users")
    public UserPolicyPaginatedResponse getUsersByPolicyId(
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
            @Valid @RequestBody SupportTicketUpdateRequest request) {
        log.info("Updating ticket status to {} for ticketId: {} with response: {}", request.getStatus(), ticketId, request.getResponse());
        SupportTicket ticket = supportTicketService.updateTicketStatus(ticketId, request);
        log.info("Ticket {} status updated to {}", ticketId, ticket.getStatus());
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

    @PutMapping("/claim/{claimId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> updateClaimStatus(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable Long claimId,
                                                           @Valid @RequestBody ClaimStatusUpdateRequest request) {
        log.info("Admin updating claim status for claim ID: {} to status: {}", claimId, request.getStatus());
        ClaimResponse response = claimService.updateClaimStatus(userPrincipal.getId(), claimId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimPaginatedResponse> getAllClaims(@RequestParam(required = false) Optional<ClaimStatus> status,
                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Admin fetching all claims");
        ClaimPaginatedResponse claims = claimService.getAllClaims(status.orElse(null), page, size);
        return ResponseEntity.ok(claims);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sendNotifications/user")
    public ResponseEntity<Void> sendNotificationsBulk(@Valid @RequestBody NotificationSendBulkRequest request) {
        notificationService.sendNotificationsBulk(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sendNotifications/policy")
    public ResponseEntity<Void> sendNotificationByPolicy(@Valid @RequestBody NotificationByPolicyRequest request) {
        log.info("Sending notification by policy ID: {}", request.getPolicyId());
        notificationService.sendNotificationByPolicy(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activeUsers")
    public ResponseEntity<UserPaginatedResponse> getActiveUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Received request for get all users");
        UserPaginatedResponse users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }
}
