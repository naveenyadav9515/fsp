package com.enterprise.taskmanager.security;

/*
 * ============================================================
 * JWT FILTER — Checks Every Request for a Valid Token
 * ============================================================
 *
 * WHAT IS A FILTER?
 * A Filter is like a "security guard" at the gate.
 * Every HTTP request passes through this filter BEFORE reaching
 * the controller.
 *
 * WHAT DOES THIS FILTER DO?
 * 1. Checks if the request has an "Authorization" header
 * 2. If yes, extracts the JWT token from the header
 * 3. Validates the token (is it real? has it expired?)
 * 4. If valid, tells Spring Security "this user is authenticated"
 * 5. If invalid or missing, the request continues without authentication
 *    (Spring Security will decide if that's OK based on the security config)
 *
 * THE FLOW:
 *   Client Request → JWT Filter → Security Config → Controller
 *
 * The Authorization header looks like:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * ============================================================
 */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// OncePerRequestFilter = This filter runs exactly ONCE per request (not multiple times)
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    // We need JwtUtil to validate tokens
    private final JwtUtil jwtUtil;

    // Constructor Injection (Spring automatically provides JwtUtil)
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /*
     * This method runs for EVERY incoming HTTP request.
     *
     * @param request  The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param chain    The chain of filters (we must call chain.doFilter to continue)
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        // STEP 1: Get the "Authorization" header from the request
        String authHeader = request.getHeader("Authorization");

        // STEP 2: Check if the header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // STEP 3: Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);  // "Bearer ".length() = 7

            try {
                // STEP 4: Extract the username from the token
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // STEP 5: Validate the token
                if (username != null && jwtUtil.isTokenValid(token, username)) {

                    // STEP 6: Create an Authentication object
                    // This tells Spring Security: "This user is authenticated!"
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,                // Principal (who)
                                    null,                    // Credentials (password - not needed, we have the token)
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority(role)  // Authorities (what they can do)
                                    )
                            );

                    // STEP 7: Store the authentication in Spring's SecurityContext
                    // Now any part of the app can check who the current user is
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("User '{}' authenticated with role '{}'", username, role);
                }
            } catch (Exception ex) {
                // If the token is invalid (expired, tampered, etc.)
                // We just log it and let the request continue without authentication.
                // Spring Security will deny access if authentication is required.
                logger.warn("Invalid JWT token: {}", ex.getMessage());
            }
        }

        // STEP 8: Continue to the next filter (or the controller)
        // This line MUST be called, otherwise the request gets stuck!
        chain.doFilter(request, response);
    }
}
