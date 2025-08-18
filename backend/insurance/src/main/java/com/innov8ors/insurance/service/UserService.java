package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;

public interface UserService {
    User register(UserCreateRequest userCreateRequest);

    String login(UserLoginRequest userLoginRequest);
}
