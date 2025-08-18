package com.innov8ors.insurance.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class InsuranceUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static Boolean matchPasswordAndHash(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }

    public static ModelMapper MAPPER = new ModelMapper();
    public static ObjectMapper objectMapper = new ObjectMapper();
}
