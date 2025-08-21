package com.innov8ors.insurance.util;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.entity.Policy;
import com.innov8ors.insurance.entity.SupportTicket;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.entity.UserPolicy;
import com.innov8ors.insurance.enums.ClaimStatus;
import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.enums.Role;
import com.innov8ors.insurance.enums.SupportTicketStatus;
import com.innov8ors.insurance.enums.UserPolicyStatus;
import com.innov8ors.insurance.request.ClaimCreateRequest;
import com.innov8ors.insurance.request.ClaimStatusUpdateRequest;
import com.innov8ors.insurance.request.NotificationByPolicyRequest;
import com.innov8ors.insurance.request.NotificationRequest;
import com.innov8ors.insurance.request.NotificationSendBulkRequest;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.request.PolicyCreateRequest;
import com.innov8ors.insurance.request.PolicyPurchaseRequest;
import com.innov8ors.insurance.request.SupportTicketCreateRequest;
import com.innov8ors.insurance.request.SupportTicketUpdateRequest;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.request.UserLoginRequest;
import com.innov8ors.insurance.response.ClaimPaginatedResponse;
import com.innov8ors.insurance.response.ClaimResponse;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.innov8ors.insurance.util.InsuranceUtil.encodePassword;

public class TestUtil {
    public static final Long TEST_USER_ID = 600900L;
    public static final Long TEST_ADMIN_ID = 600901L;
    public static final String TEST_USER_NAME = "testUser";
    public static final String TEST_ADMIN_NAME = "testAdmin";
    public static final String TEST_USER_EMAIL = "testUser@gmail.com";
    public static final String TEST_ADMIN_EMAIL = "testAdmin@gmail.com";
    public static final String TEST_USER_PASSWORD = "testPassword";
    public static final String TEST_ADMIN_PASSWORD = "testAdminPassword";
    public static final String TEST_USER_PASSWORD_HASH = encodePassword(TEST_USER_PASSWORD);
    public static final String TEST_ADMIN_PASSWORD_HASH = encodePassword(TEST_ADMIN_PASSWORD);
    public static final String TEST_USER_PHONE = "1234567890";
    public static final String TEST_ADMIN_PHONE = "0987654321";
    public static final String TEST_USER_ADDRESS = "123 Test Street, Test City, TC 12345";
    public static final String TEST_ADMIN_ADDRESS = "456 Admin Avenue, Admin City, AC 67890";
    public static final Role TEST_USER_ROLE = Role.USER;
    public static final Role TEST_ADMIN_ROLE = Role.ADMIN;
    public static final String TEST_TOKEN = "testToken";
    public static final Long TEST_POLICY_ID = 1L;
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
    public static final Long TEST_CLAIM_ID = 2L;
    public static final LocalDate TEST_CLAIM_DATE = LocalDate.now();
    public static final BigDecimal TEST_CLAIM_AMOUNT = BigDecimal.valueOf(500.0);
    public static final String TEST_CLAIM_REASON = "Test claim reason";
    public static final ClaimStatus TEST_CLAIM_STATUS = ClaimStatus.PENDING;
    public static final String TEST_CLAIM_REVIEWER_COMMENT = "Initial claim submission";
    public static final Long TEST_NOTIFICATION_ID = 1L;
    public static final String TEST_NOTIFICATION_MESSAGE = "Test notification message";
    public static final NotificationType TEST_NOTIFICATION_TYPE = NotificationType.GENERAL;
    public static final NotificationStatus TEST_NOTIFICATION_STATUS = NotificationStatus.UNREAD;
    public static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now();
    public static final String TEST_SUPPORT_TICKET_SUBJECT = "Test Support Subject";
    public static final String TEST_SUPPORT_TICKET_DESCRIPTION = "This is a test support ticket description.";
    public static final Long TEST_SUPPORT_TICKET_ID = 420L;
    public static final SupportTicketStatus TEST_SUPPORT_TICKET_STATUS = SupportTicketStatus.OPEN;
    public static final String TEST_SUPPORT_TICKET_RESPONSE = "This is a test response to the support ticket.";


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

    public static User getAdmin() {
        return User.builder()
                .id(TEST_ADMIN_ID)
                .name(TEST_ADMIN_NAME)
                .email(TEST_ADMIN_EMAIL)
                .passwordHash(TEST_ADMIN_PASSWORD_HASH)
                .phone(TEST_ADMIN_PHONE)
                .address(TEST_ADMIN_ADDRESS)
                .role(Role.ADMIN)
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
                .user(getUser())
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
                        .user(getUser())
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
                        .user(getUser())
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
                .user(getUser())
                .build();
    }

    public static PolicyPurchaseRequest getPolicyPurchaseRequest() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest();
        request.setPolicyId(TEST_POLICY_ID);
        request.setPremiumPaid(TEST_POLICY_PREMIUM_PAID);
        return request;
    }

    public static PolicyPurchaseRequest getPolicyPurchaseRequestWithPremium() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest();
        request.setPolicyId(TEST_POLICY_ID);
        request.setPremiumPaid(TEST_POLICY_PREMIUM_PAID);
        return request;
    }

    public static ClaimCreateRequest getClaimCreateRequest() {
        return ClaimCreateRequest.builder()
                .policyId(TEST_POLICY_ID)
                .claimDate(TEST_CLAIM_DATE)
                .claimAmount(TEST_CLAIM_AMOUNT)
                .reason(TEST_CLAIM_REASON)
                .build();
    }

    public static Claim getClaim() {
        return Claim.builder()
                .id(TEST_CLAIM_ID)
                .userPolicyId(TEST_USER_POLICY_ID)
                .claimDate(TEST_CLAIM_DATE)
                .claimAmount(TEST_CLAIM_AMOUNT)
                .reason(TEST_CLAIM_REASON)
                .status(TEST_CLAIM_STATUS)
                .reviewerComment(TEST_CLAIM_REVIEWER_COMMENT)
                .userPolicy(getUserPolicy())
                .build();
    }

    public static ClaimResponse getClaimResponse() {
        return ClaimResponse.builder()
                .id(TEST_CLAIM_ID)
                .userPolicyId(TEST_USER_POLICY_ID)
                .claimDate(TEST_CLAIM_DATE)
                .claimAmount(TEST_CLAIM_AMOUNT)
                .reason(TEST_CLAIM_REASON)
                .status(TEST_CLAIM_STATUS)
                .reviewerComment(TEST_CLAIM_REVIEWER_COMMENT)
                .policyName(TEST_POLICY_NAME)
                .policyType(TEST_POLICY_TYPE)
                .build();
    }

    public static ClaimPaginatedResponse getClaimPaginatedResponse() {
        return ClaimPaginatedResponse.builder()
                .claims(List.of(getClaimResponse()))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();
    }

    public static ClaimStatusUpdateRequest getClaimStatusUpdateRequest() {
        return ClaimStatusUpdateRequest.builder()
                .status(ClaimStatus.APPROVED)
                .reviewerComment(TEST_CLAIM_REVIEWER_COMMENT)
                .build();
    }

    public static Page<Claim> getClaimsPage() {
        return new PageImpl<>(List.of(getClaim()));
    }

    public static Notification getNotification() {
        return Notification.builder()
                .id(TEST_NOTIFICATION_ID)
                .message(TEST_NOTIFICATION_MESSAGE)
                .type(TEST_NOTIFICATION_TYPE)
                .status(TEST_NOTIFICATION_STATUS)
                .createdAt(TEST_CREATED_AT)
                .readAt(null)
                .user(getUser())
                .build();
    }

    public static NotificationSendRequest getNotificationSendRequest() {
        return NotificationSendRequest.builder()
                .userId(TEST_USER_ID)
                .message(TEST_NOTIFICATION_MESSAGE)
                .type(TEST_NOTIFICATION_TYPE)
                .build();
    }

    public static NotificationRequest getNotificationRequest() {
        return NotificationRequest.builder()
                .message(TEST_NOTIFICATION_MESSAGE)
                .type(TEST_NOTIFICATION_TYPE)
                .build();
    }

    public static NotificationSendBulkRequest getNotificationSendBulkRequest() {
        return NotificationSendBulkRequest.builder()
                .userId(List.of(TEST_USER_ID))
                .request(getNotificationRequest())
                .build();
    }

    public static List<Notification> getNotifications() {
        return List.of(getNotification());
    }

    public static NotificationByPolicyRequest getNotificationByPolicyRequest() {
        return NotificationByPolicyRequest.builder()
                .policyId(TEST_POLICY_ID)
                .request(getNotificationRequest())
                .build();
    }

    public static UserPolicyPaginatedResponse getUserPolicyPaginatedResponse() {
        return UserPolicyPaginatedResponse.builder()
                .userPolicies(List.of(getUserPolicyResponse()))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    private static UserPolicyResponse getUserPolicyResponse() {
        return UserPolicyResponse.builder()
                .id(TEST_USER_POLICY_ID)
                .policyId(TEST_POLICY_ID)
                .policyName(TEST_POLICY_NAME)
                .policyType(TEST_POLICY_TYPE)
                .startDate(TEST_POLICY_START_DATE)
                .endDate(TEST_POLICY_END_DATE)
                .status(TEST_USER_POLICY_STATUS)
                .premiumPaid(TEST_POLICY_PREMIUM_PAID)
                .totalAmountClaimed(BigDecimal.ZERO) // Assuming no claims made yet
                .userId(TEST_USER_ID)
                .userName(TEST_USER_NAME)
                .userEmail(TEST_USER_EMAIL)
                .userPhone(TEST_USER_PHONE)
                .userAddress(TEST_USER_ADDRESS)
                .coverageAmount(TEST_POLICY_COVERAGE_AMOUNT)
                .build();
    }

    public static SupportTicket getSupportTicket(SupportTicketStatus status) {
        return getSupportTicket(TEST_SUPPORT_TICKET_ID, TEST_SUPPORT_TICKET_SUBJECT, TEST_SUPPORT_TICKET_STATUS);
    }

    public static SupportTicket getSupportTicket(Long id, String subject, SupportTicketStatus status) {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(id);
        ticket.setSubject(subject);
        ticket.setStatus(status);
        return ticket;
    }

    public static SupportTicket createSupportTicket(Long id, Long userId, String subject, SupportTicketStatus status) {
        SupportTicket ticket = new SupportTicket();
        ticket.setId(id);
        ticket.setUserId(userId);
        ticket.setSubject(subject);
        ticket.setStatus(status);
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        return ticket;
    }

    public static SupportTicketCreateRequest getSupportTicketCreateRequest() {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest();
        request.setPolicyId(TEST_POLICY_ID);
        request.setClaimId(TEST_CLAIM_ID);
        request.setSubject(TEST_SUPPORT_TICKET_SUBJECT);
        request.setDescription(TEST_SUPPORT_TICKET_DESCRIPTION);
        return request;
    }

    public static SupportTicketUpdateRequest getSupportTicketUpdateRequest() {
        return SupportTicketUpdateRequest.builder()
                .response(TEST_SUPPORT_TICKET_RESPONSE)
                .status(SupportTicketStatus.RESOLVED)
                .build();
    }
}
