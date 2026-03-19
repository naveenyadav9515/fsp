package com.enterprise.taskmanager.service;

/*
 * ============================================================
 * AUTH SERVICE — Handles Registration and Login Logic
 * ============================================================
 *
 * WHAT IS THE SERVICE LAYER?
 * The Service layer contains the BUSINESS LOGIC of your application.
 * It sits BETWEEN the Controller (HTTP) and the Repository (Database).
 *
 *   Controller → calls → Service → calls → Repository → Database
 *
 * WHY NOT PUT LOGIC IN THE CONTROLLER?
 * 1. Reusability: Multiple controllers can use the same service
 * 2. Testing:     Services are easier to test (no HTTP involved)
 * 3. Separation:  Each layer has ONE responsibility
 *
 * THIS SERVICE HANDLES:
 *   - User Registration (create a new account)
 *   - User Login (verify credentials and return a JWT token)
 *
 * ============================================================
 */

import com.enterprise.taskmanager.dto.LoginRequest;
import com.enterprise.taskmanager.dto.LoginResponse;
import com.enterprise.taskmanager.dto.RegisterRequest;
import com.enterprise.taskmanager.model.User;
import com.enterprise.taskmanager.repository.UserRepository;
import com.enterprise.taskmanager.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// @Service tells Spring: "This is a service class — manage its lifecycle for me"
@Service
public class AuthService {

    // Logger: Used to print informational messages to the console
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // Dependencies (injected by Spring via constructor)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /*
     * CONSTRUCTOR INJECTION (Dependency Injection)
     *
     * Instead of creating these objects ourselves with "new",
     * Spring creates them and passes them to us.
     * This is called "Dependency Injection" — one of Spring's core features!
     *
     * WHY? It makes the code:
     *   1. Easier to test (you can pass mock objects)
     *   2. Loosely coupled (not tied to specific implementations)
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /*
     * REGISTER A NEW USER
     * ────────────────────
     * Steps:
     *   1. Check if the username already exists
     *   2. Hash the password (NEVER store plain text passwords!)
     *   3. Save the user to the database
     *   4. Return a success message
     */
    public String register(RegisterRequest request) {
        logger.info("Attempting to register user: {}", request.getUsername());

        // Step 1: Check if username is already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken!");
        }

        // Step 2: Create a new User object
        User user = new User();
        user.setUsername(request.getUsername());

        // Step 3: Hash the password before saving
        // encode() converts "password123" → "$2a$10$xR3qLm..."
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Step 4: Set default role (every new user is a regular USER)
        user.setRole("ROLE_USER");

        // Step 5: Save to database
        userRepository.save(user);

        logger.info("User '{}' registered successfully!", request.getUsername());
        return "User registered successfully!";
    }

    /*
     * LOGIN A USER
     * ────────────
     * Steps:
     *   1. Find the user by username
     *   2. Check if the password matches
     *   3. Generate a JWT token
     *   4. Return the token to the client
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());

        // Step 1: Find the user in the database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        // Step 2: Check if the provided password matches the stored hash
        // matches() compares "password123" with "$2a$10$xR3qLm..."
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        // Step 3: Generate a JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        logger.info("User '{}' logged in successfully!", user.getUsername());

        // Step 4: Return the token + user info
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}
