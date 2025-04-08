package com.example.rolebasedauth.Service;

import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Entity.Role;
import java.util.List;
import java.util.Set;

public interface UserService {
    // Create operations
    UserDto createUser(UserCreateDto userCreateDto);
    
    // Read operations
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    
    // Update operations
    UserDto updateUser(Long id, UserDto userDto);
    UserDto updateUserRoles(Long userId, Set<Role.ERole> roles);
    void updateLastLogin(String username);
    
    // Delete operations
    void deleteUser(Long id);
    void softDeleteUser(Long id);
    
    // Additional methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserDto> findInactiveUsers();
    List<UserDto> findUsersByRole(Role.ERole roleName);
}