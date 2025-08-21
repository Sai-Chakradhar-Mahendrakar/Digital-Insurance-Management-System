package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.response.UserPaginatedResponse;
import com.innov8ors.insurance.service.ClaimService;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.service.PolicyService;
import com.innov8ors.insurance.service.SupportTicketService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static com.innov8ors.insurance.util.TestUtil.TEST_ADMIN_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_SUPPORT_TICKET_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_SUPPORT_TICKET_STATUS;
import static com.innov8ors.insurance.util.TestUtil.getClaimPaginatedResponse;
import static com.innov8ors.insurance.util.TestUtil.getClaimResponse;
import static com.innov8ors.insurance.util.TestUtil.getClaimStatusUpdateRequest;
import static com.innov8ors.insurance.util.TestUtil.getNotificationByPolicyRequest;
import static com.innov8ors.insurance.util.TestUtil.getNotificationSendBulkRequest;
import static com.innov8ors.insurance.util.TestUtil.getPolicy;
import static com.innov8ors.insurance.util.TestUtil.getPolicyCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicket;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicketUpdateRequest;
import static com.innov8ors.insurance.util.TestUtil.getUserPaginatedResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest {
    private AdminController adminController;
    @Mock
    private PolicyService policyService;

    @Mock
    private SupportTicketService supportTicketService;

    @Mock
    private UserPolicyService userPolicyService;

    @Mock
    private ClaimService claimService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        adminController = new AdminController(policyService, userPolicyService, supportTicketService, claimService, notificationService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserPrincipal mockPrincipal = Mockito.mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(TEST_ADMIN_ID);

        authentication = new UsernamePasswordAuthenticationToken(
                mockPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testSuccessfulAddPolicy() throws Exception {
        Policy mockResponse = getPolicy();
        doReturn(mockResponse)
                .when(policyService)
                .addPolicy(any());

        this.mockMvc.perform(post("/admin/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getPolicyCreateRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(policyService).addPolicy(getPolicyCreateRequest());
        verifyNoMoreInteractions(policyService);
    }

    @Test
    public void testSuccessfulUpdateTicketStatus() throws Exception {
        SupportTicket mockResponse = getSupportTicket(TEST_SUPPORT_TICKET_STATUS);
        doReturn(mockResponse)
                .when(supportTicketService)
                .updateTicketStatus(any(Long.class), any());

        this.mockMvc.perform(patch("/admin/support/{ticketId}", TEST_SUPPORT_TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSupportTicketUpdateRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(supportTicketService).updateTicketStatus(TEST_SUPPORT_TICKET_ID, getSupportTicketUpdateRequest());
        verifyNoMoreInteractions(supportTicketService);
    }

    @Test
    public void testSuccessfulFetchAllTickets() throws Exception {
        List<SupportTicket> mockResponse = List.of(getSupportTicket(TEST_SUPPORT_TICKET_STATUS));
        doReturn(mockResponse)
                .when(supportTicketService)
                .fetchAllTickets();

        this.mockMvc.perform(get("/admin/support/fetchAll")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(supportTicketService).fetchAllTickets();
        verifyNoMoreInteractions(supportTicketService);
    }

    @Test
    public void testSuccessfulUpdateClaimStatus() throws Exception {
        ClaimResponse mockResponse = getClaimResponse();
        doReturn(mockResponse)
                .when(claimService)
                .updateClaimStatus(any(), any());

        this.mockMvc.perform(put("/admin/claim/{claimId}/status", TEST_CLAIM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getClaimStatusUpdateRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(claimService).updateClaimStatus(TEST_CLAIM_ID, getClaimStatusUpdateRequest());
        verifyNoMoreInteractions(claimService);
    }

    @Test
    public void testSuccessfulGetAllClaims() throws Exception {
        ClaimPaginatedResponse mockResponse = getClaimPaginatedResponse();
        doReturn(mockResponse)
                .when(claimService)
                .getAllClaims(any(), any(), any());

        this.mockMvc.perform(get("/admin/claims")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(claimService).getAllClaims(null, 0, 10);
        verifyNoMoreInteractions(claimService);
    }

    @Test
    public void testSuccessfulSendNotificationsBulk() throws Exception {
        doNothing()
                .when(notificationService)
                .sendNotificationsBulk(any());

        this.mockMvc.perform(post("/admin/sendNotifications/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getNotificationSendBulkRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(notificationService).sendNotificationsBulk(getNotificationSendBulkRequest());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    public void testSuccessfulSendNotificationByPolicy() throws Exception {
        doNothing()
                .when(notificationService)
                .sendNotificationByPolicy(any());

        this.mockMvc.perform(post("/admin/sendNotifications/policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getNotificationByPolicyRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(notificationService).sendNotificationByPolicy(getNotificationByPolicyRequest());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    public void testSuccessfulGetActiveUsers() throws Exception {
        UserPaginatedResponse mockResponse = getUserPaginatedResponse();
        doReturn(mockResponse)
                .when(userService)
                .getAllUsers(any(), any());

        this.mockMvc.perform(get("/admin/activeUsers")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(0, 10);
        verifyNoMoreInteractions(userService);
    }
}
