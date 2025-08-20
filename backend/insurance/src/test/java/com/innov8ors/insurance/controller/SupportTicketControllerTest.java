package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.UserPrincipal;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
        public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
            org.springframework.security.core.userdetails.UserDetails user = org.springframework.security.core.userdetails.User.withUsername("test@example.com")
                    .password("password")
                    .authorities("ROLE_USER", "ROLE_ADMIN")
                    .build();
            return new org.springframework.security.provisioning.InMemoryUserDetailsManager(user);
        }
    }

    @Test
    void testCreateTicket() throws Exception {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest();
        request.setPolicyId(123L);
        request.setClaimId(456L);
        request.setSubject("Need help");
        request.setDescription("I have a question.");
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        ticket.setSubject("Need help");
        Mockito.when(supportTicketService.createTicket(Mockito.any(), Mockito.anyLong())).thenReturn(ticket);

        com.innov8ors.insurance.entity.User user = new com.innov8ors.insurance.entity.User();
        user.setId(99L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setPhone("1234567890");
        user.setAddress("Test Address");
        user.setRole(Role.USER);
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
    void testGetTicketsByUser() throws Exception {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        Mockito.when(supportTicketService.getTicketsByUser(1L)).thenReturn(List.of(ticket));

        mockMvc.perform(MockMvcRequestBuilders.get("/support/user/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L));
    }

    @Test
    void testUpdateTicketStatus() throws Exception {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        ticket.setStatus(SupportTicketStatus.RESOLVED);
        Mockito.when(supportTicketService.updateTicketStatus(1L, "Resolved", SupportTicketStatus.RESOLVED)).thenReturn(ticket);

        com.innov8ors.insurance.entity.User user = new com.innov8ors.insurance.entity.User();
        user.setId(99L);
        user.setName("Test Admin");
        user.setEmail("admin@example.com");
        user.setPasswordHash("password");
        user.setPhone("1234567890");
        user.setAddress("Admin Address");
        user.setRole(Role.ADMIN);
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.put("/support/1")
                .param("response", "Resolved")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
