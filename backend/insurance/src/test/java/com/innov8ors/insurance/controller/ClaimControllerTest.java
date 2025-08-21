package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static com.innov8ors.insurance.util.TestUtil.TEST_CLAIM_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_EMAIL;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_PASSWORD;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ROLE;
import static com.innov8ors.insurance.util.TestUtil.getClaimCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getClaimPaginatedResponse;
import static com.innov8ors.insurance.util.TestUtil.getClaimResponse;
import static com.innov8ors.insurance.util.TestUtil.getUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;

@WebMvcTest(ClaimController.class)
@Import(ClaimControllerTest.TestConfig.class)
@ContextConfiguration(classes = {ClaimController.class, ClaimControllerTest.TestSecurityConfig.class})
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(claimService);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ClaimService claimService() {
            return Mockito.mock(ClaimService.class);
        }
    }

    @TestConfiguration
    @Configuration
    static class TestSecurityConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails user = User.withUsername(TEST_USER_EMAIL)
                    .password(TEST_USER_PASSWORD)
                    .authorities("ROLE_USER", "ROLE_ADMIN")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitClaimSuccessfully() throws Exception {
        ClaimCreateRequest request = getClaimCreateRequest();
        ClaimResponse response = getClaimResponse();

        Mockito.when(claimService.submitClaim(any(ClaimCreateRequest.class), anyLong()))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/claim")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TEST_CLAIM_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.claimAmount").value(500.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value("Test claim reason"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"));
    }

    @Test
    void submitClaimWithoutAuthentication() throws Exception {
        ClaimCreateRequest request = getClaimCreateRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/claim")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitClaimWithInvalidRequest() throws Exception {
        ClaimCreateRequest request = getClaimCreateRequest();
        request.setReason(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/claim")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.user(TEST_USER_EMAIL).roles(TEST_USER_ROLE.name()))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitClaimWithZeroAmount() throws Exception {
        ClaimCreateRequest request = getClaimCreateRequest();
        request.setClaimAmount(BigDecimal.ZERO);

        mockMvc.perform(MockMvcRequestBuilders.post("/claim")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.user(TEST_USER_EMAIL).roles(TEST_USER_ROLE.name()))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void submitClaimWithNegativeAmount() throws Exception {
        ClaimCreateRequest request = getClaimCreateRequest();
        request.setClaimAmount(BigDecimal.valueOf(-100));

        mockMvc.perform(MockMvcRequestBuilders.post("/claim")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.user(TEST_USER_EMAIL).roles(TEST_USER_ROLE.name()))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserClaimsSuccessfully() throws Exception {
        ClaimPaginatedResponse response = getClaimPaginatedResponse();

        Mockito.when(claimService.getUserClaims(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/claims")
                .param("page", "0")
                .param("size", "10")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.claims").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.claims[0].id").value(TEST_CLAIM_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserClaimsWithDefaultPagination() throws Exception {
        ClaimPaginatedResponse response = getClaimPaginatedResponse();

        Mockito.when(claimService.getUserClaims(TEST_USER_ID, 0, 10))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/claims")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(claimService, Mockito.times(1)).getUserClaims(TEST_USER_ID, 0, 10);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserClaimsWithCustomPagination() throws Exception {
        ClaimPaginatedResponse response = getClaimPaginatedResponse();

        Mockito.when(claimService.getUserClaims(TEST_USER_ID, 2, 5))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/claims")
                .param("page", "2")
                .param("size", "5")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(claimService, Mockito.times(1)).getUserClaims(TEST_USER_ID, 2, 5);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserClaimsWhenEmpty() throws Exception {
        ClaimPaginatedResponse emptyResponse = ClaimPaginatedResponse.builder()
                .claims(Collections.emptyList())
                .totalElements(0L)
                .totalPages(0)
                .size(10)
                .page(0)
                .build();

        Mockito.when(claimService.getUserClaims(anyLong(), anyInt(), anyInt()))
                .thenReturn(emptyResponse);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/claims")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.claims").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    void getUserClaimsWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/claims"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClaimByIdSuccessfully() throws Exception {
        ClaimResponse response = getClaimResponse();

        Mockito.when(claimService.getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/claim/{claimId}", TEST_CLAIM_ID)
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TEST_CLAIM_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.claimAmount").value(500.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason").value("Test claim reason"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getClaimByIdWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/claim/{claimId}", TEST_CLAIM_ID))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClaimByIdVerifiesUserOwnership() throws Exception {
        ClaimResponse response = getClaimResponse();

        Mockito.when(claimService.getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID))
                .thenReturn(response);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get("/claim/{claimId}", TEST_CLAIM_ID)
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(claimService, Mockito.times(1)).getByClaimIdAndUserId(TEST_CLAIM_ID, TEST_USER_ID);
    }
}
