package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.service.SupportTicketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

@WebMvcTest(SupportTicketController.class)
@ContextConfiguration(classes = {SupportTicketController.class, SupportTicketControllerTest.TestSecurityConfig.class})
class SupportTicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportTicketService supportTicketService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    @Configuration
    static class TestSecurityConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails user = User.withUsername("test@example.com")
                    .password("password")
                    .authorities("ROLE_USER", "ROLE_ADMIN")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

    private SupportTicket createSupportTicket(Long id, String subject, SupportTicketStatus status) {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(id);
        ticket.setSubject(subject);
        ticket.setStatus(status);
        return ticket;
    }

    private com.innov8ors.insurance.entity.User createUser(Long id, String name, String email, Role role) {
        com.innov8ors.insurance.entity.User user = new com.innov8ors.insurance.entity.User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash("password");
        user.setPhone("1234567890");
        user.setAddress("Test Address");
        user.setRole(role);
        return user;
    }

    @Test
    void testCreateTicket() throws Exception {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest();
        request.setPolicyId(123L);
        request.setClaimId(456L);
        request.setSubject("Need help");
        request.setDescription("I have a question.");
        SupportTicket ticket = createSupportTicket(1L, "Need help", SupportTicketStatus.OPEN);
        Mockito.when(supportTicketService.createTicket(Mockito.any(), Mockito.anyLong())).thenReturn(ticket);

        com.innov8ors.insurance.entity.User user = createUser(99L, "Test User", "test@example.com", Role.USER);
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/support")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateTicket_InvalidInput() throws Exception {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest(); // missing required fields
        mockMvc.perform(MockMvcRequestBuilders.post("/support")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetTicketsByUser() throws Exception {
        SupportTicket ticket = createSupportTicket(1L, "Need help", SupportTicketStatus.OPEN);
        Mockito.when(supportTicketService.getTicketsByUser(1L)).thenReturn(List.of(ticket));

        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetTicketsByUser_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/getTicketsByUser"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetTicketsByUser_NotFound() throws Exception {
        Mockito.when(supportTicketService.getTicketsByUser(999L)).thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/999"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void testUpdateTicketStatus() throws Exception {
        SupportTicket ticket = createSupportTicket(1L, "Need help", SupportTicketStatus.RESOLVED);
        SupportTicketUpdateRequest updateRequest = new SupportTicketUpdateRequest();
        updateRequest.setResponse("Resolved");
        updateRequest.setStatus(SupportTicketStatus.RESOLVED);
        Mockito.when(supportTicketService.updateTicketStatus(1L, updateRequest)).thenReturn(ticket);

        com.innov8ors.insurance.entity.User user = createUser(99L, "Test Admin", "admin@example.com", Role.ADMIN);
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.put("/support/1")
                .param("response", "Resolved")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTicketStatus_NotFound() throws Exception {
        SupportTicketUpdateRequest updateRequest = new SupportTicketUpdateRequest();
        updateRequest.setResponse("Resolved");
        updateRequest.setStatus(SupportTicketStatus.RESOLVED);
        Mockito.when(supportTicketService.updateTicketStatus(Mockito.anyLong(), Mockito.eq(updateRequest))).thenThrow(new RuntimeException("Support ticket not found"));
        mockMvc.perform(MockMvcRequestBuilders.put("/support/999")
                .param("response", "Resolved"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
