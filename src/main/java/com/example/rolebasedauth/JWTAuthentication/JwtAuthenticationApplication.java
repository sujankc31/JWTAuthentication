package com.example.rolebasedauth.JWTAuthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.rolebasedauth")
@EnableJpaRepositories("com.example.rolebasedauth.Repository")
@EntityScan("com.example.rolebasedauth.Entity")
@ComponentScan(basePackages = {
    "com.example.rolebasedauth.Security",
    "com.example.rolebasedauth.Controller",
    "com.example.rolebasedauth.Service",
    "com.example.rolebasedauth.Repository"
})
public class JwtAuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtAuthenticationApplication.class, args);
    }
}
