package com.example.rolebasedauth.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Service.AuthenticationService;
import com.example.rolebasedauth.Service.RoleService;
import com.example.rolebasedauth.Service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/user")
public class UserStatsController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationService authenticationService;
    private static final java.util.logging.Logger log = java.util.logging.Logger
            .getLogger(UserStatsController.class.getName());

    public UserStatsController(UserRepository userRepository, UserService userService, AuthenticationService authenticationService, RoleService roleService) {
        this.roleService=roleService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping("/users-stats")
    @ResponseBody
    public Map<String, Integer> getUserStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("activeUsers", userService.getTotalActiveUsers());
        stats.put("inactiveUsers", userService.getTotalInactiveUsers());
        return stats;
    }

    @GetMapping("/user-list")
    @ResponseBody
    public List<UserDto> getUserList() {

        return userService.getAllUsers();
    }

    /**
     * Returns the active status of a user identified by {@code id}.
     * 
     * @param id the ID of the user to retrieve
     * @return a ResponseEntity containing the active status of the user, or
     *         404 if the user is not found
     */
    @GetMapping("/user-status/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> getUserStatus(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return userService.getUserById(id) != null ? ResponseEntity.ok(user.getIsActive())
                : ResponseEntity.notFound().build();
    }
    /**
     * Toggle the active status of a user identified by {@code id}.
     * 
     * @param id the ID of the user to toggle
     * @return a ResponseEntity containing the new active status of the user, or
     *         404 if the user is not found
     */
    @PostMapping("/change-user-status/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleUserStatus(@PathVariable Long id) {
        try {
            UserDto updatedUser = userService.updateUserStatus(id);
            return ResponseEntity.ok(updatedUser.getIsActive());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

/**
 * Change the password of a user identified by {@code id}.
 * 
 * @param id the ID of the user whose password is to be changed
 * @param newPassword the new password for the user
 * @param confirmPassword the confirmation of the new password
 * @return a ResponseEntity indicating the result of the operation
 */
@PostMapping("/change-password/{id}")
@ResponseBody
public ResponseEntity<String> changePassword(@PathVariable Long id, 
                                             @RequestParam String newPassword, 
                                             @RequestParam String confirmPassword) {
    
    
    // Perform standard password validation
    if (newPassword.length() < 6 || newPassword.length() > 40) {
        return ResponseEntity.badRequest().body("Password must be between 6 and 40 characters");
    }
    String pattern = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()-_=+\\|\\[\\]{};:'\",<.>/?]).{6,40}";
    if (!newPassword.matches(pattern)) {
        return ResponseEntity.badRequest().body("Password must contain at least one lowercase letter, one uppercase letter, one number and one special character");
    }
    if (!newPassword.equals(confirmPassword)) {
        return ResponseEntity.badRequest().body("Passwords do not match");
    }
    

    try {
        authenticationService.resetUserPassword(id, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not change password");
    }
}

@GetMapping("/edit-user/{id}")
public ResponseEntity<UserDto> showEditUserForm(@PathVariable Long id) {
    UserDto userDto = userService.getUserById(id);
    if (userDto == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(userDto);
}

@GetMapping("/all-roles")
@ResponseBody
public ResponseEntity<List<Role>> getAllRoles() {
    try {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PatchMapping("/edit-user/{id}")
@ResponseBody
public ResponseEntity<UserDto> editUser(@PathVariable Long id, @RequestBody UserDto userDto) {
    try {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

@GetMapping("/user-info/{id}")
@ResponseBody
public ResponseEntity<UserDto> getUserInfo(@PathVariable Long id) {
    try {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

@PostMapping("/admin/add-role")
public ResponseEntity<String> createRole(@RequestBody Role role) {
    try {
        roleService.createRole(role);
        return ResponseEntity.ok("Role added successfully!");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Error adding role: " + e.getMessage());
    }
}

@PostMapping("/admin/add-user")
public ResponseEntity<?> addUser(@RequestBody UserCreateDto userCreateDto) {
    try {
        UserDto user = userService.addUser(userCreateDto);
        return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

}
