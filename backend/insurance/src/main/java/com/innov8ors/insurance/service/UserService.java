package com.innov8ors.insurance.service;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.UserRegisterResponse;

import java.util.List;

public interface UserService {
    UserRegisterResponse register(UserCreateRequest userCreateRequest);

    String login(UserLoginRequest userLoginRequest);

    Boolean validateIfUserExists(String email);

    User getByEmail(String email);

    User getById(Long id);

    List<User> getAllUsers();
}
