package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.exception.UnauthorizedException;
import com.innov8ors.insurance.repository.dao.UserDao;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.service.TokenService;
import com.innov8ors.insurance.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.INCORRECT_PASSWORD;
import static com.innov8ors.insurance.util.Constant.ErrorMessage.USER_NOT_FOUND;
import static com.innov8ors.insurance.util.TestUtil.TEST_TOKEN;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ADDRESS;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_EMAIL;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ID;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_NAME;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_PASSWORD_HASH;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_PHONE;
import static com.innov8ors.insurance.util.TestUtil.TEST_USER_ROLE;
import static com.innov8ors.insurance.util.TestUtil.getUser;
import static com.innov8ors.insurance.util.TestUtil.getUserCreateRequest;
import static com.innov8ors.insurance.util.TestUtil.getUserLoginRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserServiceImplTest {
    UserService userService;
    @Mock
    UserDao userDao;

    @Mock
    TokenService tokenService;

    AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
        userService = new UserServiceImpl(tokenService, userDao);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSuccessfulRegister() {
        doReturn(getUser())
                .when(userDao)
                .persist(any(User.class));

        User user = userService.register(getUserCreateRequest());

        assertNotNull(user);
        assertEquals(TEST_USER_ID, user.getId());
        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_EMAIL, user.getEmail());
        assertEquals(TEST_USER_PASSWORD_HASH, user.getPasswordHash());
        assertEquals(TEST_USER_PHONE, user.getPhone());
        assertEquals(TEST_USER_ADDRESS, user.getAddress());
    }

    @Test
    public void testFailureRegister() {
        doThrow(new RuntimeException("error"))
                .when(userDao)
                .persist(any(User.class));

        User user = null;
        try {
            user = userService.register(getUserCreateRequest());
            fail("Expected an exception to be thrown");
        } catch (RuntimeException ignored) {
            assertEquals("error", ignored.getMessage());
        }
        assertNull(user);
        verify(userDao).persist(any(User.class));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    public void testSuccessfulLogin() {
        doReturn(Optional.of(getUser()))
                .when(userDao)
                .getByEmail(TEST_USER_EMAIL);
        doReturn(TEST_TOKEN)
                .when(tokenService)
                .generateToken(any(), any());

        String token = userService.login(getUserLoginRequest());

        assertNotNull(token);
        assertEquals(TEST_TOKEN, token);
        verify(userDao).getByEmail(TEST_USER_EMAIL);
        verify(tokenService).generateToken(TEST_USER_EMAIL, TEST_USER_ROLE.name());
        verifyNoMoreInteractions(userDao, tokenService);
    }

    @Test
    public void testFailureLoginDueToUserNotFound() {
        doReturn(Optional.empty())
                .when(userDao)
                .getByEmail(TEST_USER_EMAIL);

        try {
            userService.login(getUserLoginRequest());
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertInstanceOf(NotFoundException.class, e);
            assertEquals(USER_NOT_FOUND, e.getMessage());
        }

        verify(userDao).getByEmail(TEST_USER_EMAIL);
        verifyNoMoreInteractions(userDao, tokenService);
    }

    @Test
    public void testFailureLoginDueToIncorrectPassword() {
        UserLoginRequest userLoginRequest = getUserLoginRequest();
        userLoginRequest.setPassword("wrongPassword");
        doReturn(Optional.of(getUser()))
                .when(userDao)
                .getByEmail(TEST_USER_EMAIL);
        String token = null;

        try {
            token = userService.login(userLoginRequest);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertInstanceOf(UnauthorizedException.class, e);
            assertEquals(INCORRECT_PASSWORD, e.getMessage());
        }

        assertNull(token);
        verify(userDao).getByEmail(TEST_USER_EMAIL);
        verifyNoMoreInteractions(userDao, tokenService);
    }
}
