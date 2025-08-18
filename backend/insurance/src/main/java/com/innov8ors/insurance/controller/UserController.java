package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.LoginResponse;
import com.innov8ors.insurance.response.UserCreateResponse;
import com.innov8ors.insurance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.innov8ors.insurance.mapper.UserMapper.getLoginResponseFromToken;
import static com.innov8ors.insurance.mapper.UserMapper.getRegisterResponseFromUser;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public UserCreateResponse register(@Valid @RequestBody UserCreateRequest user) {
        User registeredUser = userService.register(user);
        return getRegisterResponseFromUser(registeredUser);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest);
        return getLoginResponseFromToken(token);
    }
}
