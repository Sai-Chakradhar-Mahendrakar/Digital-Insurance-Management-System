package com.innov8ors.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.UserRegisterResponse;
import com.innov8ors.insurance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.innov8ors.insurance.util.TestUtil.TEST_TOKEN;
import static com.innov8ors.insurance.util.TestUtil.getUserCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getUserLoginRequest;
import static com.innov8ors.insurance.util.TestUtil.getUserRegisterResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testSuccessfulRegister() throws Exception {
        UserRegisterResponse mockResponse = getUserRegisterResponse();
        doReturn(mockResponse)
                .when(userService)
                .register(any(UserCreateRequest.class));

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUserCreateRequest())))
                .andExpect(status().isOk());

        verify(userService).register(any(UserCreateRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        doReturn(TEST_TOKEN)
                .when(userService)
                .login(any(UserLoginRequest.class));

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUserLoginRequest())))
                .andExpect(status().isOk());

        verify(userService).login(any(UserLoginRequest.class));
        verifyNoMoreInteractions(userService);
    }
}
