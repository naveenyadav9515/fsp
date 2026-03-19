package com.enterprise.taskmanager.dto;

/*
 * ============================================================
 * TASK REQUEST DTO — What the User Sends to Create/Update a Task
 * ============================================================
 *
 * WHAT IS A DTO?
 * DTO stands for "Data Transfer Object".
 * It's a simple class used to carry data between the client (frontend)
 * and the server (backend).
 *
 * WHY NOT USE THE TASK MODEL DIRECTLY?
 * 3 important reasons:
 *   1. SECURITY: The user should NOT be able to set fields like "id" or "createdAt"
 *   2. VALIDATION: We only validate what the user SENDS, not what's in the DB
 *   3. SEPARATION: The API contract is separate from the database schema
 *
 * VALIDATION ANNOTATIONS:
 *   @NotBlank  → The field cannot be null, empty, or just whitespace
 *   @Size      → The field must be between min and max characters
 *   @NotNull   → The field cannot be null (but CAN be empty)
 *
 * These annotations are checked automatically by Spring Boot
 * when you use @Valid in the controller!
 *
 * ============================================================
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskRequestDto {

    // ---- FIELDS ----

    // Title is required and must be between 3 and 100 characters
    @NotBlank(message = "Title is required! Please provide a task title.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    // Description is optional, but if provided, cannot exceed 500 characters
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Priority is required when creating a task
    @NotNull(message = "Priority is required! Choose LOW, MEDIUM, or HIGH.")
    private String priority;

    // ---- CONSTRUCTORS ----

    // Default constructor
    public TaskRequestDto() {
    }

    // ---- GETTERS AND SETTERS ----

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
}
