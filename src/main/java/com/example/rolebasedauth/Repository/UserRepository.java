package com.example.rolebasedauth.Repository;

import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Existence check methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Custom query methods
    @Query("SELECT u FROM User u WHERE u.lastLogin < :cutoffDate")
    List<User> findInactiveUsers(LocalDateTime cutoffDate);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(Role.ERole roleName);
    
    // Count methods
    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = 0", nativeQuery = true)
    Integer countInactiveUsers();

    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = 1", nativeQuery = true)
    Integer countActiveUsers();


    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    Integer countTotalUsers();


    long countByIsActiveTrue();
    long countByIsActiveFalse();
    
}