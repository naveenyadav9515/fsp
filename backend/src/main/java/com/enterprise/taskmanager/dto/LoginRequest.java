package com.enterprise.taskmanager.dto;

/*
 * ============================================================
 * LOGIN REQUEST DTO — What the User Sends to Login
 * ============================================================
 *
 * Used at: POST /api/auth/login
 *
 * The user sends their username and password.
 * If correct, the server returns a JWT token.
 *
 * ============================================================
 */

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    private String password;

    // ---- CONSTRUCTORS ----
    public LoginRequest() {
    }

    // ---- GETTERS AND SETTERS ----

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
