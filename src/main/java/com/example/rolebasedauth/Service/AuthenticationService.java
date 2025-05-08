package com.example.rolebasedauth.Service;

import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;

public interface AuthenticationService {
    // Authentication methods
    String authenticateUser(String username, String password);
    UserDto registerNewUser(UserCreateDto userCreateDto);
    void resetUserPassword(Long id, String newPassword);
    void resetPassword(String email);
}