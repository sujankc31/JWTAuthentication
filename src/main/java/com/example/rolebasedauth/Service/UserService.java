package com.example.rolebasedauth.Service;

import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    // Create operations
    UserDto createUser(UserCreateDto userCreateDto);
    
    UserDto addUser(UserCreateDto userCreateDto);
    
    // Read operations
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();

    // Update operations
    UserDto updateUserStatus(Long id);
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

    /**
     * Gets the total number of active users.
     *
     * @return The total number of active users.
     */
    //
    Integer getTotalActiveUsers();
    /**
     * Gets the total number of inactive users.
     *
     * @return The total number of inactive users.
     */
    //
    Integer getTotalInactiveUsers();
    /**
     * Gets the total number of all users, regardless of their active status.
     *
     * @return The total number of all users.
     */
    //
    Integer getTotalUsers();

    UserDto editProfile(User user, String email);

    UserDto editUser(UserDto userDto);

}