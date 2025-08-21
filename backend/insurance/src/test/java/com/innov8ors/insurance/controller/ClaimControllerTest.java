package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.getClaimCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getClaimPaginatedResponse;
import static com.innov8ors.insurance.util.TestUtil.getClaimResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class ClaimControllerTest {
    @Mock
    private ClaimService claimService;

    private ClaimController claimController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        claimController = new ClaimController(claimService);
        mockMvc = MockMvcBuilders.standaloneSetup(claimController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserPrincipal mockPrincipal = Mockito.mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(TEST_USER_ID);

        authentication = new UsernamePasswordAuthenticationToken(
                mockPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testSubmitClaim() throws Exception {
        ClaimResponse mockResponse = getClaimResponse();
        when(claimService.submitClaim(Mockito.any(), Mockito.eq(TEST_USER_ID)))
                .thenReturn(mockResponse);

        this.mockMvc.perform(post("/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getClaimCreateRequest()))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isCreated());

        verify(claimService).submitClaim(any(), eq(TEST_USER_ID));
        verifyNoMoreInteractions(claimService);
    }

    @Test
    public void testSuccessfulGetUserClaims() throws Exception {
        ClaimPaginatedResponse mockResponse = getClaimPaginatedResponse();
        when(claimService.getUserClaims(any(), any(), any()))
                .thenReturn(mockResponse);

        this.mockMvc.perform(get("/user/claims")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(claimService).getUserClaims(TEST_USER_ID, 0, 10);
        verifyNoMoreInteractions(claimService);
    }

    @Test
    public void testGetClaimById() throws Exception {
        ClaimResponse mockResponse = getClaimResponse();
        when(claimService.getByClaimIdAndUserId(any(), any()))
                .thenReturn(mockResponse);

        this.mockMvc.perform(get("/claim/{claimId}", TEST_CLAIM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk());

        verify(claimService).getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);
        verifyNoMoreInteractions(claimService);
    }
}
