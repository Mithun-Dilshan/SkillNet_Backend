package com.skillshare.skill_platform.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/users/**").permitAll() // Allow public access to user endpoints for testing
                .requestMatchers("/api/auth/**").permitAll() // Also allow auth endpoints
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/auth/success", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/api/public/logout").permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Disable CSRF for API testing

        return http.build();
    }
}