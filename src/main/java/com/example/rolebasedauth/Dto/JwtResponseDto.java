package com.example.rolebasedauth.Dto;

import java.util.Set;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}