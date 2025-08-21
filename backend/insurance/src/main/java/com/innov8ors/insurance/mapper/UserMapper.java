package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.response.UserPaginatedResponse;
import com.innov8ors.insurance.response.LoginResponse;
import com.innov8ors.insurance.response.UserRegisterResponse;
import org.springframework.data.domain.Page;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;
import static com.innov8ors.insurance.util.InsuranceUtil.encodePassword;

public class UserMapper {

    public static UserRegisterResponse getRegisterResponseFromUser(User user, String token) {
        UserRegisterResponse userRegisterResponse = MAPPER.map(user, UserRegisterResponse.class);
        userRegisterResponse.setToken(token);
        return userRegisterResponse;
    }

    public static LoginResponse getLoginResponseFromToken(String token) {
        return LoginResponse.builder()
                .token(token)
                .build();
    }

    public static User getUserFromRequest(UserCreateRequest userCreateRequest) {
        User createdUser = MAPPER.map(userCreateRequest, User.class);
        createdUser.setPasswordHash(encodePassword(userCreateRequest.getPassword()));
        return createdUser;
    }

    public static UserPaginatedResponse getPaginatedResponseFromUser(Page<User> users, Integer page, Integer size) {
        return UserPaginatedResponse.builder()
                .users(users.getContent())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .size(size)
                .page(page)
                .build();
    }
}
