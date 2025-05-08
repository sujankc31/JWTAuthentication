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
import java.util.regex.Pattern;
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


    /**
     * Update the active status of a user identified by {@code id}.
     * 
     * @param id the ID of the user whose active status is to be updated
     * @return a UserDto containing the updated active status of the user, or
     *         404 if the user is not found
     */
    @Override
    @Transactional
    public UserDto updateUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean newStatus = !user.getIsActive();
        user.setIsActive(newStatus);
        User updatedUser = userRepository.save(user);
        return convertToUserDto(updatedUser);
    }
    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {  
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());

        if (userDto.getNewPassword() != null && !userDto.getNewPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        }   
        if (userDto.getIsActive() != null) {
        boolean newStatus = !existingUser.getIsActive();
        existingUser.setIsActive(newStatus);
        }
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : userDto.getRoles()) {
                Role role = roleRepository.findByName(Role.ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            existingUser.setRoles(roles);
        }

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
   
    @Override
    public Integer getTotalActiveUsers() {
        Integer activeUserCount = userRepository.countActiveUsers(); // Replace with your logic to count active users
        return (activeUserCount != null) ? activeUserCount : 0;
    }
    @Override
    public Integer getTotalInactiveUsers() {
        Integer inactiveUserCount = userRepository.countInactiveUsers(); // Replace with your logic to count active users
        return (inactiveUserCount != null) ? inactiveUserCount : 0;
    }

    @Override
    public Integer getTotalUsers() {
        Integer totalUserCount = userRepository.countTotalUsers(); // Replace with your logic to count total users
        return (totalUserCount != null) ? totalUserCount : 0;
    }

    @Override
    @Transactional
    public UserDto editProfile(User user, String email) {
        user.setEmail(email);
       
        User updatedUser = userRepository.save(user);
        return convertToUserDto(updatedUser);
    }
    @Override
    @Transactional
    public UserDto addUser(UserCreateDto userCreateDto) {
        String username = userCreateDto.getUsername();

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username cannot be empty!");
        }
        
        // Check if username has any uppercase letters
        if (!username.equals(username.toLowerCase())) {
            throw new RuntimeException("Username must not contain any uppercase letters!");
        }
        
        // Check for invalid characters
        if (!username.matches("^[a-z0-9_]*$")) {
            throw new RuntimeException("Username must only contain lowercase letters, numbers, and underscores!");
        }
        
        // Check for minimum length
        if (username.length() < 6) {
            throw new RuntimeException("Username must be at least 6 characters long!");
        }
        

        // Email validation pattern
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        // Check if email is valid
        if (userCreateDto.getEmail() == null || !emailPattern.matcher(userCreateDto.getEmail()).matches()) {
            throw new RuntimeException("Invalid email format! Example: example@domain.com");
        }
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
        // Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
        // .orElseThrow(() -> new RuntimeException("Default role not found"));

        // user.setRoles(Set.of(userRole)); // Assign default role

        // Handle roles from form
        Set<String> strRoles = userCreateDto.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Optional fallback to default role
            Role defaultRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        } else {
            for (String roleName : strRoles) {
                Role.ERole roleEnum = Role.ERole.valueOf(roleName);
                Role role = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        System.out.println("Saving user...");
        User savedUser = userRepository.save(user);

        // Convert and return UserDto
        return convertToUserDto(savedUser);
    }
    

@Override
@Transactional
public UserDto editUser(UserDto userDto) {
    User existingUser = userRepository.findById(userDto.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    existingUser.setUsername(userDto.getUsername());
    existingUser.setEmail(userDto.getEmail());
    
    if (userDto.getNewPassword() != null && !userDto.getNewPassword().isEmpty()) {
        existingUser.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
    }
    
    if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : userDto.getRoles()) {
            Role role = roleRepository.findByName(Role.ERole.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        existingUser.setRoles(roles);
    }

    User updatedUser = userRepository.save(existingUser);
    return convertToUserDto(updatedUser);
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

