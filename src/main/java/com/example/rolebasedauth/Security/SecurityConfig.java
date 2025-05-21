package com.example.rolebasedauth.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.rolebasedauth.Service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity() // Enable debug to see which configuration is being appliedgit ls-remote --tags origin

public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityContext(context -> context.requireExplicitSave(false))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/change-user-status/**","/total-active-users","/active-user-test","/login","/login1","/dashboard", "/register", "/contact", "/about","/api/auth/**", 
                               "/homepage", "/custom-login", "/css/**","/user-status/**","/user/**","/admin/**","/actuator/**","/robots.txt", "/sitemap.xml","/captcha","/verify-captcha","/webfonts/**",
                               "/js/**", "/images/**", "/error","/page-not-found", "/?continue").permitAll()
                .requestMatchers("/dashboard").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/custom-login")
                .loginProcessingUrl("/api/auth/login1")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/custom-login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/homepage")
                .permitAll()
            );
        
        return http.build();
    }

}