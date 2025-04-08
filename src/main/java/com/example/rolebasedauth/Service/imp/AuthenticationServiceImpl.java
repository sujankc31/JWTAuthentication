package com.example.rolebasedauth.Service.imp;

import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Entity.User;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Service.UserService;
import com.example.rolebasedauth.Service.AuthenticationService;
import com.example.rolebasedauth.Service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Update last login
        userService.updateLastLogin(username);

        // Generate JWT token
        return jwtService.generateToken(authentication);
    }

    @Override
    @Transactional
    public UserDto registerNewUser(UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate temporary password
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // TODO: Send email with temporary password
        // sendPasswordResetEmail(email, tempPassword);
    }

    // Generate a random temporary password
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }
}