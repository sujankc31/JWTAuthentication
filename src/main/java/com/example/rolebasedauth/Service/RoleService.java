package com.example.rolebasedauth.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.example.rolebasedauth.Entity.Role;

public interface RoleService {
    // Role-related operations
    Set<Role> assignRolesToUser(Long userId, Set<Role.ERole> roles);
    void createRole(Role role);
    Optional<Role> findByName(Role.ERole roleName);
    List<Role> getAllRoles();
}