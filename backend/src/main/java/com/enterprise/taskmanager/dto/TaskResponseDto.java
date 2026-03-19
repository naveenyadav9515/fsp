package com.enterprise.taskmanager.dto;

/*
 * ============================================================
 * TASK RESPONSE DTO — What We Send Back to the User
 * ============================================================
 *
 * WHY A SEPARATE RESPONSE DTO?
 * We might NOT want to expose every database field to the client.
 * For example, we might want to hide internal IDs or sensitive data.
 *
 * In this case, our response is similar to the model, but the key
 * concept is: you CONTROL what the client sees.
 *
 * REAL-WORLD EXAMPLE:
 * A User model might have a "password" field, but the Response DTO
 * should NEVER include it! That's why we use separate DTOs.
 *
 * ============================================================
 */

import java.time.LocalDateTime;

public class TaskResponseDto {

    // ---- FIELDS ----
    private String id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;

    // ---- CONSTRUCTORS ----

    // Default constructor
    public TaskResponseDto() {
    }

    // ---- GETTERS AND SETTERS ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
