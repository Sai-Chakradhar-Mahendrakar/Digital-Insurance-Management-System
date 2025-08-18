package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.error.InsuranceServiceErrorType;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.exception.UnauthorizedException;
import com.innov8ors.insurance.repository.dao.UserDao;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.service.TokenService;
import com.innov8ors.insurance.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.innov8ors.insurance.mapper.UserMapper.getUserFromRequest;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.INCORRECT_PASSWORD;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_NOT_FOUND;
import static com.innov8ors.insurance.util.InsuranceUtil.matchPasswordAndHash;

@Service
public class UserServiceImpl implements UserService {

    private final TokenService tokenService;

    private final UserDao userDao;

    public UserServiceImpl(TokenService tokenService, UserDao userDao) {
        this.tokenService = tokenService;
        this.userDao = userDao;
    }

    @Override
    public User register(UserCreateRequest userCreateRequest) {
        User user = getUserFromRequest(userCreateRequest);
        return userDao.persist(user);
    }

    @Override
    public String login(UserLoginRequest userLoginRequest) {
        Optional<User> userOptional = userDao.getByEmail(userLoginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!matchPasswordAndHash(userLoginRequest.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(INCORRECT_PASSWORD, InsuranceServiceErrorType.UNAUTHORIZED);
        }
        return tokenService.generateToken(user.getEmail(), user.getRole().name());
    }
}