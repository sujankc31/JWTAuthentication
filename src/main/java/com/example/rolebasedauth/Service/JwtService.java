package com.example.rolebasedauth.Service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    // JWT-related operations
    String generateToken(Authentication authentication);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    Long getExpirationFromToken(String token);
}