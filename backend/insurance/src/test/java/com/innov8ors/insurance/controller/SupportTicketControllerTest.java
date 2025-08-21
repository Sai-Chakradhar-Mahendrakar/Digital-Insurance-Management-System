package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
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

import java.util.List;

import static com.innov8ors.insurance.util.TestUtil.TEST_ADMIN_ROLE;
import static com.innov8ors.insurance.util.TestUtil.TEST_SUPPORT_TICKET_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_EMAIL;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_PASSWORD;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ROLE;
import static com.innov8ors.insurance.util.TestUtil.getAdmin;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicket;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicketCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getSupportTicketUpdateRequest;
import static com.innov8ors.insurance.util.TestUtil.getUser;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(SupportTicketController.class)
@Import(SupportTicketControllerTest.TestConfig.class)
@ContextConfiguration(classes = {SupportTicketController.class, SupportTicketControllerTest.TestSecurityConfig.class})
class SupportTicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupportTicketService supportTicketService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SupportTicketService supportTicketService() {
            return Mockito.mock(SupportTicketService.class);
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

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
    void testCreateTicket() throws Exception {
        SupportTicketCreateRequest request = getSupportTicketCreateRequest();
        SupportTicket ticket = getSupportTicket(SupportTicketStatus.OPEN);
        Mockito.when(supportTicketService.createTicket(any(), Mockito.anyLong())).thenReturn(ticket);

        com.innov8ors.insurance.entity.User user = getUser();
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/support")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateTicket_InvalidInput() throws Exception {
        SupportTicketCreateRequest request = getSupportTicketCreateRequest();
        request.setSubject(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/support")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetTicketsByUser() throws Exception {
        SupportTicket ticket = getSupportTicket(SupportTicketStatus.OPEN);
        Mockito.when(supportTicketService.getTicketsByUser(TEST_SUPPORT_TICKET_ID)).thenReturn(List.of(ticket));

        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/{supportTicketId}", TEST_SUPPORT_TICKET_ID)
                .with(SecurityMockMvcRequestPostProcessors.user(TEST_USER_EMAIL).roles(TEST_USER_ROLE.name())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(TEST_SUPPORT_TICKET_ID));
    }

    @Test
    void testGetTicketsByUser_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/getTicketsByUser"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetTicketsByUser_NotFound() throws Exception {
        Mockito.when(supportTicketService.getTicketsByUser(TEST_USER_ID)).thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/{userId}", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void testUpdateTicketStatus() throws Exception {
        SupportTicket ticket = getSupportTicket(SupportTicketStatus.RESOLVED);
        SupportTicketUpdateRequest updateRequest = getSupportTicketUpdateRequest();
        Mockito.when(supportTicketService.updateTicketStatus(TEST_SUPPORT_TICKET_ID, updateRequest)).thenReturn(ticket);

        mockMvc.perform(MockMvcRequestBuilders.put("/support/{ticketId}", TEST_SUPPORT_TICKET_ID)
                .param("response", "Resolved")
                .with(SecurityMockMvcRequestPostProcessors.user(TEST_USER_EMAIL).roles(TEST_ADMIN_ROLE.name())))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTicketStatus_NotFound() throws Exception {
        SupportTicketUpdateRequest updateRequest = getSupportTicketUpdateRequest();
        Mockito.when(supportTicketService.updateTicketStatus(Mockito.anyLong(), Mockito.eq(updateRequest))).thenThrow(new RuntimeException("Support ticket not found"));
        mockMvc.perform(MockMvcRequestBuilders.put("/support/{ticketId}", TEST_SUPPORT_TICKET_ID)
                .param("response", "Resolved"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
