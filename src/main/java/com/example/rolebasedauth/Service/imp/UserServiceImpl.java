package com.example.rolebasedauth.Service.imp;

import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Entity.User;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Repository.RoleRepository;
import com.example.rolebasedauth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;     
    @Override
    @Transactional
    // public UserDto createUser(UserCreateDto userCreateDto) {
    //     // Check if username or email already exists
    //     if (userRepository.existsByUsername(userCreateDto.getUsername())) {
    //         throw new RuntimeException("Username is already taken!");
    //     }
    //     if (userRepository.existsByEmail(userCreateDto.getEmail())) {
    //         throw new RuntimeException("Email is already in use!");
    //     }

    //     // Create user Entity
    //     User user = User.builder()
    //             .username(userCreateDto.getUsername())
    //             .email(userCreateDto.getEmail())
    //             .password(passwordEncoder.encode(userCreateDto.getPassword()))
    //             .build();

    //     // Assign roles
    //     Set<Role> roles = new HashSet<>();
    //     if (userCreateDto.getRoles() == null || userCreateDto.getRoles().isEmpty()) {
    //         // Default to USER role if no roles specified
    //         Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
    //                 .orElseThrow(() -> new RuntimeException("Default role not found"));
    //         roles.add(userRole);
    //     } else {
    //         for (String roleName : userCreateDto.getRoles()) {
    //             Role role = roleRepository.findByName(Role.ERole.valueOf(roleName))
    //                     .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    //             roles.add(role);
    //         }
    //     }

    //     user.setRoles(roles);
    //     User savedUser = userRepository.save(user);

    //     // Convert and return UserDto
    //     return convertToUserDto(savedUser);
    // }

    public UserDto createUser(UserCreateDto userCreateDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
    
        // Create user Entity
        User user = User.builder()
                .username(userCreateDto.getUsername())
                .email(userCreateDto.getEmail())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();
    
        // Assign default USER role
        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        user.setRoles(Set.of(userRole));  // Assign default role
    
        User savedUser = userRepository.save(user);
    
        // Convert and return UserDto
        return convertToUserDto(savedUser);
    }
    
    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(existingUser);
        return convertToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto updateUserRoles(Long userId, Set<Role.ERole> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> newRoles = roles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        User updatedUser = userRepository.save(user);
        return convertToUserDto(updatedUser);
    }

    @Override
    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserDto> findInactiveUsers() {
        return userRepository.findInactiveUsers(LocalDateTime.now().minusMonths(6)).stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findUsersByRole(Role.ERole roleName) {
        return userRepository.findUsersByRoleName(roleName).stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert User entity to UserDto
    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .build();
    }
}

