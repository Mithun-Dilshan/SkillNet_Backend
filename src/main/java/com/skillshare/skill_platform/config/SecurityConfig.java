package com.skillshare.skill_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for API endpoints
            .cors(cors -> cors.configure(http))     // Enable CORS
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/users/**").permitAll() // Allow public access to user endpoints for testing
                // Post endpoints
                .requestMatchers(
                    "/api/posts",
                    "/api/posts/**"
                ).permitAll()  // Allow public access to post endpoints
                // Comment endpoints
                .requestMatchers(
                    "/api/comments/**",
                    "/api/comments/{postId}",
                    "/api/comments/{commentId}/{userId}"
                ).permitAll()  // Allow public access to comment endpoints
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