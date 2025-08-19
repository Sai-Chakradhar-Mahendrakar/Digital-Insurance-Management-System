package com.innov8ors.insurance.service;

import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.UserRegisterResponse;

public interface UserService {
    UserRegisterResponse register(UserCreateRequest userCreateRequest);

    String login(UserLoginRequest userLoginRequest);

    Boolean validateIfUserExists(String email);
}
