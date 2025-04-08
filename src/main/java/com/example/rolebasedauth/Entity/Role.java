package com.example.rolebasedauth.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @Column(name = "description")
    private String description;

    public enum ERole {
        ROLE_USER("Standard User"),
        ROLE_MODERATOR("Content Moderator"),
        ROLE_ADMIN("System Administrator");

        private final String description;

        ERole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
