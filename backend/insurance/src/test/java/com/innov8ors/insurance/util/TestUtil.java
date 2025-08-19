package com.innov8ors.insurance.util;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.innov8ors.insurance.util.InsuranceUtil.encodePassword;

public class TestUtil {
    public static final Long TEST_USER_ID = 600900L;
    public static final String TEST_USER_NAME = "testUser";
    public static final String TEST_USER_EMAIL = "testUser@gmail.com";
    public static final String TEST_USER_PASSWORD = "testPassword";
    public static final String TEST_USER_PASSWORD_HASH = encodePassword(TEST_USER_PASSWORD);
    public static final String TEST_USER_PHONE = "1234567890";
    public static final String TEST_USER_ADDRESS = "123 Test Street, Test City, TC 12345";
    public static final Role TEST_USER_ROLE = Role.USER;
    public static final String TEST_TOKEN = "testToken";
    public static final Long TEST_POLICY_ID = 1L;
    public static final String TEST_POLICY_NAME = "Test Policy";
    public static final String TEST_POLICY_TYPE = "Health";
    public static final String TEST_POLICY_DESCRIPTION = "This is a test policy description.";
    public static final Double TEST_POLICY_PREMIUM_AMOUNT = 100.0;
    public static final Double TEST_POLICY_COVERAGE_AMOUNT = 10000.0;
    public static final Integer TEST_POLICY_DURATION_MONTHS = 12; // Assuming a default duration of 12 months
    public static final Long TEST_USER_POLICY_ID = 2L;
    public static final LocalDateTime TEST_POLICY_START_DATE = LocalDateTime.now();
    public static final LocalDateTime TEST_POLICY_END_DATE = LocalDateTime.now().plusMonths(TEST_POLICY_DURATION_MONTHS);
    public static final UserPolicyStatus TEST_USER_POLICY_STATUS = UserPolicyStatus.ACTIVE;
    public static final BigDecimal TEST_POLICY_PREMIUM_PAID = BigDecimal.valueOf(TEST_POLICY_PREMIUM_AMOUNT);


    public static User getUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .name(TEST_USER_NAME)
                .email(TEST_USER_EMAIL)
                .passwordHash(TEST_USER_PASSWORD_HASH)
                .phone(TEST_USER_PHONE)
                .address(TEST_USER_ADDRESS)
                .role(TEST_USER_ROLE)
                .build();
    }

    public static UserCreateRequest getUserCreateRequest() {
        return UserCreateRequest.builder()
                .name(TEST_USER_NAME)
                .email(TEST_USER_EMAIL)
                .password(TEST_USER_PASSWORD)
                .phone(TEST_USER_PHONE)
                .address(TEST_USER_ADDRESS)
                .role(TEST_USER_ROLE)
                .build();
    }

    public static UserLoginRequest getUserLoginRequest() {
        return UserLoginRequest.builder()
                .email(TEST_USER_EMAIL)
                .password(TEST_USER_PASSWORD)
                .build();
    }

    public static Policy getPolicy() {
        return getPolicy(TEST_POLICY_ID);
    }

    public static Policy getPolicy(Long id) {
        return Policy.builder()
                .id(id)
                .name(TEST_POLICY_NAME)
                .type(TEST_POLICY_TYPE)
                .description(TEST_POLICY_DESCRIPTION)
                .premiumAmount(TEST_POLICY_PREMIUM_AMOUNT)
                .coverageAmount(TEST_POLICY_COVERAGE_AMOUNT)
                .durationMonths(TEST_POLICY_DURATION_MONTHS)
                .renewalPremiumRate(TEST_POLICY_PREMIUM_AMOUNT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Page<Policy> getPoliciesPage() {
        return new PageImpl<>(Collections.nCopies(10, getPolicy()));
    }

    public static PolicyCreateRequest getPolicyCreateRequest() {
        return PolicyCreateRequest.builder()
                .name(TEST_POLICY_NAME)
                .type(TEST_POLICY_TYPE)
                .description(TEST_POLICY_DESCRIPTION)
                .premiumAmount(TEST_POLICY_PREMIUM_AMOUNT)
                .coverageAmount(TEST_POLICY_COVERAGE_AMOUNT)
                .durationMonths(TEST_POLICY_DURATION_MONTHS)
                .renewalPremiumRate(TEST_POLICY_PREMIUM_AMOUNT)
                .build();
    }

    public static UserPolicy getUserPolicy() {
        return UserPolicy.builder()
                .id(TEST_USER_POLICY_ID)
                .userId(TEST_USER_ID)
                .policyId(TEST_POLICY_ID)
                .startDate(TEST_POLICY_START_DATE)
                .endDate(TEST_POLICY_END_DATE)
                .status(TEST_USER_POLICY_STATUS)
                .premiumPaid(TEST_POLICY_PREMIUM_PAID)
                .build();
    }


}
