package com.example.rolebasedauth.Dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Integer id;
    private String name;
    private String description;
}