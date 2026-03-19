package com.enterprise.taskmanager.controller;

/*
 * ============================================================
 * AUTH CONTROLLER — Login and Registration API Endpoints
 * ============================================================
 *
 * WHAT IS A CONTROLLER?
 * A Controller is the "front door" of your application.
 * It receives HTTP requests from the client (frontend, Postman, curl)
 * and sends back HTTP responses.
 *
 * CONTROLLER RULES:
 *   1. NEVER put business logic here (that belongs in the Service layer)
 *   2. Only translate HTTP requests → call Service → translate response
 *   3. Think of it as a "receptionist" — it takes requests and forwards them
 *
 * THIS CONTROLLER HANDLES:
 *   POST /api/auth/register → Create a new user account
 *   POST /api/auth/login    → Login and get a JWT token
 *
 * THESE ENDPOINTS ARE PUBLIC (no JWT token needed)
 * because obviously you can't login if you need to be logged in first! 😄
 *
 * ============================================================
 */

import com.enterprise.taskmanager.dto.LoginRequest;
import com.enterprise.taskmanager.dto.LoginResponse;
import com.enterprise.taskmanager.dto.RegisterRequest;
import com.enterprise.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * ANNOTATIONS EXPLAINED:
 *
 * @RestController = @Controller + @ResponseBody
 *   Tells Spring: "This class handles HTTP requests and returns JSON"
 *
 * @RequestMapping("/api/auth")
 *   Sets the BASE URL for all endpoints in this controller.
 *   So @PostMapping("/register") becomes: POST /api/auth/register
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // The AuthService (injected by Spring)
    private final AuthService authService;

    // Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * REGISTER ENDPOINT
     * ─────────────────
     * URL:     POST /api/auth/register
     * Body:    { "username": "john", "password": "secret123" }
     * Returns: { "message": "User registered successfully!" }
     *
     * @Valid → Triggers the validation rules in RegisterRequest
     *          (@NotBlank, @Size, etc.). If validation fails,
     *          GlobalExceptionHandler catches it automatically!
     *
     * @RequestBody → Tells Spring: "Parse the JSON body into
     *                a RegisterRequest object"
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for username: {}", request.getUsername());

        String message = authService.register(request);

        // Return 201 Created status with a success message
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.CREATED);
    }

    /*
     * LOGIN ENDPOINT
     * ──────────────
     * URL:     POST /api/auth/login
     * Body:    { "username": "john", "password": "secret123" }
     * Returns: { "token": "eyJhbG...", "username": "john", "role": "ROLE_USER" }
     *
     * The client should save the token and include it in future requests:
     *   Authorization: Bearer eyJhbG...
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for username: {}", request.getUsername());

        LoginResponse response = authService.login(request);

        // Return 200 OK with the JWT token
        return ResponseEntity.ok(response);
    }
}
