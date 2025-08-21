package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.UserPolicyService;
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

import static com.innov8ors.insurance.util.TestUtil.TEST_POLICY_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_EMAIL;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.getPolicyPurchaseRequest;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicyPaginatedResponse;
import static com.innov8ors.insurance.util.TestUtil.getUserPolicyResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserPolicyControllerTest {
    @Mock
    private UserPolicyService userPolicyService;

    private UserPolicyController userPolicyController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        userPolicyController = new UserPolicyController(userPolicyService);
        mockMvc = MockMvcBuilders.standaloneSetup(userPolicyController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserPrincipal mockPrincipal = Mockito.mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(TEST_USER_ID);
        when(mockPrincipal.getUserEmail()).thenReturn(TEST_USER_EMAIL);

        authentication = new UsernamePasswordAuthenticationToken(
                mockPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testSuccessfulPurchasePolicy() throws Exception {
        UserPolicyResponse mockResponse = getUserPolicyResponse();

        doReturn(mockResponse)
                .when(userPolicyService)
                .purchasePolicy(anyString(), any());

        this.mockMvc.perform(post("/user/policy/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getPolicyPurchaseRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isCreated());

        verify(userPolicyService).purchasePolicy(eq(TEST_USER_EMAIL), any(PolicyPurchaseRequest.class));
        verifyNoMoreInteractions(userPolicyService);
    }

    @Test
    public void testSuccessfulGetUserPolicies() throws Exception {
        UserPolicyPaginatedResponse mockResponse = getUserPolicyPaginatedResponse();
        doReturn(mockResponse)
                .when(userPolicyService)
                .getUserPolicies(TEST_USER_EMAIL, 0, 10);

        this.mockMvc.perform(get("/user/policies")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(userPolicyService).getUserPolicies(TEST_USER_EMAIL, 0, 10);
        verifyNoMoreInteractions(userPolicyService);
    }

    @Test
    public void testSuccessfulGetRenewablePolicies() throws Exception {
        UserPolicyPaginatedResponse mockResponse = getUserPolicyPaginatedResponse();
        doReturn(mockResponse)
                .when(userPolicyService)
                .getRenewablePolicies(TEST_USER_ID, 0, 10);

        this.mockMvc.perform(get("/user/policy/renewable")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(userPolicyService).getRenewablePolicies(TEST_USER_ID, 0, 10);
        verifyNoMoreInteractions(userPolicyService);
    }

    @Test
    public void testSuccessfulRenewPolicy() throws Exception {
        UserPolicyResponse mockResponse = getUserPolicyResponse();
        doReturn(mockResponse)
                .when(userPolicyService)
                .renewPolicy(any(), any());

        this.mockMvc.perform(post("/user/policy/{policyId}/renew", TEST_POLICY_ID)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(userPolicyService).renewPolicy(TEST_USER_ID, TEST_POLICY_ID);
        verifyNoMoreInteractions(userPolicyService);
    }
}
