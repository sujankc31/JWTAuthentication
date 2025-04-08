package com.example.rolebasedauth.Service.imp;

import com.example.rolebasedauth.Service.JwtService;
import com.example.rolebasedauth.Security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class JwtServiceImpl implements JwtService {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String generateToken(Authentication authentication) {
        return jwtUtils.generateJwtToken(authentication);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtils.getUserNameFromJwtToken(token);
    }

    @Override
    public Long getExpirationFromToken(String token) {
        // Implement token expiration retrieval logic
        return null; // Placeholder
    }
}