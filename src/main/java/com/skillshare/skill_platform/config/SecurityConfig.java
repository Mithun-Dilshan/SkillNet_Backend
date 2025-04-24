package com.skillshare.skill_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configure(http))
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/api/public/**").permitAll()

            // User-related endpoints (including profiles)
            .requestMatchers(
                "/api/users/**", // generic
                "/api/users/*/profile", // wildcard
                "/api/users/{userId}/profile" // explicit with variable
            ).permitAll()

            // Learning Plan endpoints (explicit and wildcard support)
            .requestMatchers(
                "/users/{user-id}/learning-plans",                         // POST, GET
                "/users/{user-id}/learning-plans/{learning-plan-id}",      // PUT, DELETE
                "/learning-plans",                                         // GET all
                "/users/*/learning-plans",                                 // wildcard POST, GET
                "/users/*/learning-plans/*"                                // wildcard PUT, DELETE
            ).permitAll()

            // All other endpoints
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/api/auth/success", true)
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/api/public/logout").permitAll()
        );

    return http.build();
  }
}
