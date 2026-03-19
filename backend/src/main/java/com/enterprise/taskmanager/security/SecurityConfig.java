package com.enterprise.taskmanager.security;

/*
 * ============================================================
 * SECURITY CONFIGURATION — Defines WHO Can Access WHAT
 * ============================================================
 *
 * WHAT IS SPRING SECURITY?
 * Spring Security is a framework that handles:
 *   1. Authentication = "WHO are you?" (login)
 *   2. Authorization  = "WHAT are you allowed to do?" (permissions)
 *
 * WHAT DOES THIS CONFIG DO?
 * - Disables CSRF (not needed for REST APIs — explained below)
 * - Defines which endpoints are PUBLIC and which need authentication
 * - Adds our JWT filter to the security chain
 * - Configures the password encoder (BCrypt)
 *
 * PUBLIC ENDPOINTS (anyone can access):
 *   /api/auth/**  → Login and Register
 *
 * PROTECTED ENDPOINTS (need JWT token):
 *   /api/tasks/** → All task operations
 *
 * WHAT IS CSRF?
 * CSRF = Cross-Site Request Forgery
 * It's a type of attack where a malicious website tricks your browser
 * into making requests to your API. We disable it because:
 *   - REST APIs use tokens (JWT), not cookies
 *   - CSRF protection is only needed for cookie-based sessions
 *
 * ============================================================
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration = "This class contains configuration settings"
// @EnableWebSecurity = "Enable Spring Security features"
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Our custom JWT filter (Spring will inject it automatically)
    private final JwtFilter jwtFilter;

    // Constructor Injection
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /*
     * SECURITY FILTER CHAIN — The main security configuration
     *
     * @Bean tells Spring: "Use this method to create an object that
     * Spring should manage. Call this once, then reuse the result."
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // STEP 1: Disable CSRF (not needed for JWT-based REST APIs)
            .csrf(csrf -> csrf.disable())

            // STEP 2: Define authorization rules
            .authorizeHttpRequests(auth -> auth
                // PUBLIC: Anyone can access authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // PROTECTED: Everything else requires authentication
                .anyRequest().authenticated()
            )

            // STEP 3: Set session management to STATELESS
            // REST APIs should NOT store sessions on the server.
            // Each request must carry its own authentication (JWT token).
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // STEP 4: Add our JWT filter BEFORE Spring's default auth filter
            // This ensures our filter runs first and sets up authentication
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * PASSWORD ENCODER — Hashes passwords using BCrypt
     *
     * WHAT IS BCrypt?
     * BCrypt is a strong hashing algorithm specifically designed for passwords.
     * It converts "password123" into something like "$2a$10$xR3qLm..."
     *
     * WHY HASH PASSWORDS?
     * If someone hacks your database, they see the HASH, not the actual password.
     * BCrypt hashes are one-way: you can't convert them back to the original password.
     *
     * HOW TO USE:
     *   encoder.encode("password123")                    → Creates the hash
     *   encoder.matches("password123", storedHash)       → Checks if they match
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
