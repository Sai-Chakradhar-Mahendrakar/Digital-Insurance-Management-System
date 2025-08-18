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
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String USER_ALREADY_EXISTS = "User already exists";
        public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
        public static final String INCORRECT_PASSWORD = "Incorrect password";
        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    }

    public static class PolicyConstants {
        public static final String POLICY_NAME_PLACEHOLDER = "name";
        public static final String POLICY_TYPE_PLACEHOLDER = "type";
    }
}
