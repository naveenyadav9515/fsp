package com.enterprise.taskmanager.dto;

/*
 * ============================================================
 * LOGIN RESPONSE DTO — What We Send Back After Successful Login
 * ============================================================
 *
 * After a successful login, we send back:
 *   1. The JWT token (used for future API calls)
 *   2. The username (so the frontend knows who is logged in)
 *   3. The role (so the frontend can show/hide features based on role)
 *
 * ============================================================
 */

public class LoginResponse {

    private String token;
    private String username;
    private String role;

    // ---- CONSTRUCTORS ----
    public LoginResponse() {
    }

    // Convenience constructor — create a response with all fields at once
    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    // ---- GETTERS AND SETTERS ----

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
