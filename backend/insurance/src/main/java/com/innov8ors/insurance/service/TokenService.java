package com.innov8ors.insurance.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
    String generateToken(String email, String role);

    String extractUserName(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
