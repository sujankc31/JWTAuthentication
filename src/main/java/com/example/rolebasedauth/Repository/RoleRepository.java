package com.example.rolebasedauth.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rolebasedauth.Entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Find methods
    Optional<Role> findByName(Role.ERole name);
    
    // Existence check methods
    boolean existsByName(Role.ERole name);
    
    // Custom query methods
    @Query("SELECT r FROM Role r WHERE r.name IN :roles")
    List<Role> findRolesByNames(List<Role.ERole> roles);
}

