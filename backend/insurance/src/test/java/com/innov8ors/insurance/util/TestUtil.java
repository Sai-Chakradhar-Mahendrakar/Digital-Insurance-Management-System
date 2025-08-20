package com.innov8ors.insurance.util;

import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public static final String USER_ALREADY_HAS_POLICY = "User already has this policy";
    public static final String TEST_POLICY_NAME = "Test Policy";
    public static final String TEST_POLICY_TYPE = "Health";
    public static final String TEST_POLICY_DESCRIPTION = "This is a test policy description.";
    public static final BigDecimal TEST_POLICY_PREMIUM_AMOUNT = BigDecimal.valueOf(100);
    public static final BigDecimal TEST_POLICY_COVERAGE_AMOUNT = BigDecimal.valueOf(10000);
    public static final Integer TEST_POLICY_DURATION_MONTHS = 12; // Assuming a default duration of 12 months
    public static final Long TEST_USER_POLICY_ID = 2L;
    public static final LocalDateTime TEST_POLICY_START_DATE = LocalDateTime.now();
    public static final LocalDateTime TEST_POLICY_END_DATE = LocalDateTime.now().plusMonths(TEST_POLICY_DURATION_MONTHS);
    public static final UserPolicyStatus TEST_USER_POLICY_STATUS = UserPolicyStatus.ACTIVE;
    public static final BigDecimal TEST_POLICY_PREMIUM_PAID = TEST_POLICY_PREMIUM_AMOUNT;


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
                .policy(getTestPolicy())
                .build();
    }

    public static User getTestUser() {
        return getUser();
    }

    public static Policy getTestPolicy() {
        return getPolicy();
    }

    public static Page<UserPolicyResponse> getUserPoliciesPage() {
        List<UserPolicyResponse> responses = Arrays.asList(
                UserPolicyResponse.builder()
                        .id(TEST_USER_POLICY_ID)
                        .policyId(TEST_POLICY_ID)
                        .policyName(TEST_POLICY_NAME)
                        .policyType(TEST_POLICY_TYPE)
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(TEST_USER_POLICY_STATUS)
                        .premiumPaid(TEST_POLICY_PREMIUM_PAID)
                        .build(),
                UserPolicyResponse.builder()
                        .id(2L)
                        .policyId(2L)
                        .policyName("Health Policy 2")
                        .policyType("Health")
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(UserPolicyStatus.ACTIVE)
                        .premiumPaid(BigDecimal.valueOf(200.0))
                        .build(),
                UserPolicyResponse.builder()
                        .id(3L)
                        .policyId(3L)
                        .policyName("Life Policy 3")
                        .policyType("Life")
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(UserPolicyStatus.ACTIVE)
                        .premiumPaid(BigDecimal.valueOf(300.0))
                        .build(),
                UserPolicyResponse.builder()
                        .id(4L)
                        .policyId(4L)
                        .policyName("Auto Policy 4")
                        .policyType("Auto")
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(UserPolicyStatus.ACTIVE)
                        .premiumPaid(BigDecimal.valueOf(400.0))
                        .build(),
                UserPolicyResponse.builder()
                        .id(5L)
                        .policyId(5L)
                        .policyName("Home Policy 5")
                        .policyType("Home")
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(UserPolicyStatus.ACTIVE)
                        .premiumPaid(BigDecimal.valueOf(500.0))
                        .build()
        );
        return new PageImpl<>(responses, PageRequest.of(0, 10), 5);
    }

    public static Page<UserPolicy> getUserPolicyPage() {
        List<UserPolicy> userPolicies = Arrays.asList(
                getUserPolicyWithPolicy(),
                UserPolicy.builder()
                        .id(2L)
                        .userId(TEST_USER_ID)
                        .policyId(2L)
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(TEST_USER_POLICY_STATUS)
                        .premiumPaid(BigDecimal.valueOf(200.0))
                        .policy(Policy.builder()
                                .id(2L)
                                .name("Health Policy 2")
                                .type("Health")
                                .build())
                        .build(),
                UserPolicy.builder()
                        .id(3L)
                        .userId(TEST_USER_ID)
                        .policyId(3L)
                        .startDate(TEST_POLICY_START_DATE)
                        .endDate(TEST_POLICY_END_DATE)
                        .status(TEST_USER_POLICY_STATUS)
                        .premiumPaid(BigDecimal.valueOf(300.0))
                        .policy(Policy.builder()
                                .id(3L)
                                .name("Life Policy 3")
                                .type("Life")
                                .build())
                        .build()
        );
        return new PageImpl<>(userPolicies, PageRequest.of(0, 10), userPolicies.size());
    }

    public static UserPolicy getUserPolicyWithPolicy() {
        return UserPolicy.builder()
                .id(TEST_USER_POLICY_ID)
                .userId(TEST_USER_ID)
                .policyId(TEST_POLICY_ID)
                .startDate(TEST_POLICY_START_DATE)
                .endDate(TEST_POLICY_END_DATE)
                .status(TEST_USER_POLICY_STATUS)
                .premiumPaid(TEST_POLICY_PREMIUM_PAID)
                .policy(getTestPolicy()) // Associate the Policy entity
                .build();
    }

    public static PolicyPurchaseRequest getPolicyPurchaseRequest() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest();
        request.setPolicyId(TEST_POLICY_ID);
        return request;
    }

    public static PolicyPurchaseRequest getPolicyPurchaseRequestWithPremium() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest();
        request.setPolicyId(TEST_POLICY_ID);
        request.setPremiumPaid(TEST_POLICY_PREMIUM_PAID);
        return request;
    }

}
