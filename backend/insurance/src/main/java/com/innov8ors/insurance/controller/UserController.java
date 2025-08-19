package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.LoginResponse;
import com.innov8ors.insurance.response.UserRegisterResponse;
import com.innov8ors.insurance.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.innov8ors.insurance.mapper.UserMapper.getLoginResponseFromToken;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public UserRegisterResponse register(@Valid @RequestBody UserCreateRequest user) {
        log.debug("Received request to register user: {}", user);
        return userService.register(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        log.debug("Received request to login user: {}", userLoginRequest);
        String token = userService.login(userLoginRequest);
        return getLoginResponseFromToken(token);
    }
}
