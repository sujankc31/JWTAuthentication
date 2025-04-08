package com.example.rolebasedauth.Service.imp;

import com.example.rolebasedauth.Entity.User;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Repository.RoleRepository;
import com.example.rolebasedauth.Service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Set<Role> assignRolesToUser(Long userId, Set<Role.ERole> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return roles;
    }

    @Override
    public void createRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public Optional<Role> findByName(Role.ERole roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

