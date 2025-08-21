package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.innov8ors.insurance.util.TestUtil.getPoliciesPage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class PolicyControllerTest {
    @Mock
    private PolicyService policyService;

    private PolicyController policyController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        policyController = new PolicyController(policyService);
        mockMvc = MockMvcBuilders.standaloneSetup(policyController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testSuccessfulGetPolicy() throws Exception {
        Page<Policy> mockResponse = getPoliciesPage();
        doReturn(mockResponse)
                .when(policyService)
                .getPolicies(any(), any(), any());

        this.mockMvc.perform(get("/policies"))
                .andExpect(status().isOk());

        verify(policyService).getPolicies(any(), any(), any());
        verifyNoMoreInteractions(policyService);
    }
}
