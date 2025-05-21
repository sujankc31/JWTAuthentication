package com.example.rolebasedauth.Repository;

import com.example.rolebasedauth.Entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    // You can add custom query methods here if needed
}
