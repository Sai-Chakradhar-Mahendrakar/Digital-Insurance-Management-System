package com.innov8ors.insurance.util;

public class Constant {
    public static final String CORE_EXCEPTION_NAME = "coreExceptionName";

    public static class CacheData {
        public static final String SESSION_DATA = "sessionData";
        public static final String STATUS = "status";
        public static final String ROLE = "role";
        public static final String USER = "user";
        public static final String ADMIN = "admin";
    }

    public static class ErrorMessage {
        public static final String USER_NOT_FOUND = "User not found";
        public static final String POLICY_NOT_FOUND = "Policy not found";
        public static final String CLAIM_NOT_FOUND = "Claim with given Id not found";
        public static final String USER_POLICY_NOT_FOUND = "User policy with given Id not found or does not belong to user";
        public static final String NOTIFICATION_NOT_FOUND_OR_DOES_NOT_BELONG_TO_USER = "Notification with given Id not found or does not belong to user";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String ONLY_PENDING_CLAIMS_CAN_BE_UPDATED = "Only pending claims can be updated";
        public static final String CANNOT_SET_STATUS_BACK_TO_PENDING = "Cannot set status back to PENDING";
        public static final String CLAIMS_ALLOWED_ONLY_FOR_ACTIVE_POLICIES = "Claims can only be submitted for active policies";
        public static final String CLAIM_DATE_NOT_WITHIN_POLICY_COVERAGE = "Claim date must be within the policy coverage period";
        public static final String CLAIM_AMOUNT_EXCEEDS_POLICY_COVERAGE = "Claim amount exceeds policy coverage amount";
        public static final String USER_ALREADY_EXISTS = "User already exists";
        public static final String USER_ALREADY_HAS_POLICY = "User has already purchased the policy";
        public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
        public static final String INCORRECT_PASSWORD = "Incorrect password";
        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    }

    public static class PolicyConstants {
        public static final String POLICY_NAME_PLACEHOLDER = "name";
        public static final String POLICY_TYPE_PLACEHOLDER = "type";
    }

    public static class UserPolicyConstants {
        public static final String START_DATE_PLACEHOLDER = "startDate";
        public static final String END_DATE_PLACEHOLDER = "endDate";
    }

    public static class ClaimConstants {
        public static final String CLAIM_DATE_PLACEHOLDER = "claimDate";
        public static final String CLAIM_STATUS_PLACEHOLDER = "status";
    }

    public static class NotificationConstants {
        public static final String NOTIFICATION_CREATED_AT_PLACEHOLDER = "createdAt";
        public static final String NOTIFICATION_TYPE_PLACEHOLDER = "type";
        public static final String NOTIFICATION_STATUS_PLACEHOLDER = "status";
        public static final String USER_PLACEHOLDER = "user";
        public static final String ID_PLACEHOLDER = "id";
    }
}
