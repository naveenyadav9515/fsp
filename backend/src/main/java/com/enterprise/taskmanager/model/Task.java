package com.enterprise.taskmanager.model;

/*
 * ============================================================
 * TASK MODEL — Represents a "Task" in our Database
 * ============================================================
 *
 * WHAT IS A MODEL?
 * A Model (also called Entity) is a Java class that represents
 * a "table" (in SQL) or a "collection" (in MongoDB).
 * Each instance of this class = one row/document in the database.
 *
 * WHAT IS @Document?
 * Since we use MongoDB (a NoSQL database), we use @Document
 * instead of @Entity (which is for SQL databases like MySQL).
 * It tells Spring: "Store objects of this class in a MongoDB collection."
 *
 * WHY NO LOMBOK?
 * We write getters/setters manually so you can see exactly what
 * the code does. In real projects, you may use Lombok's @Data
 * to auto-generate them — but for learning, explicit is better!
 *
 * ============================================================
 */

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// @Document tells MongoDB: "Create a collection called 'tasks' for this class"
@Document(collection = "tasks")
public class Task {

    // ---- FIELDS (these become the "columns" in MongoDB) ----

    // @Id marks this field as the Primary Key (unique identifier)
    // MongoDB will auto-generate a unique ID like "65f1a2b3c4d5e6f7g8h9i0j1"
    @Id
    private String id;

    // The title of the task (e.g., "Buy groceries")
    private String title;

    // A longer description (e.g., "Buy milk, eggs, and bread from the store")
    private String description;

    // The priority of the task (LOW, MEDIUM, HIGH)
    private TaskPriority priority;

    // The current status of the task
    private TaskStatus status;

    // When the task was created (set automatically)
    private LocalDateTime createdAt;

    // When the task was last updated (set automatically)
    private LocalDateTime updatedAt;

    // The ID of the user who created this task
    // This links each task to a specific user
    private String userId;

    // ---- ENUM: Task Status ----
    // An Enum is a fixed set of constants (like a dropdown menu)
    public enum TaskStatus {
        PENDING,        // Task has been created but not started
        IN_PROGRESS,    // Task is currently being worked on
        COMPLETED       // Task is done!
    }

    // ---- ENUM: Task Priority ----
    public enum TaskPriority {
        LOW,            // Not urgent
        MEDIUM,         // Somewhat important
        HIGH            // Very important, do this first!
    }

    // ---- CONSTRUCTORS ----

    // Default constructor (required by Spring/MongoDB to create objects)
    public Task() {
    }

    // ---- GETTERS AND SETTERS ----
    // These methods allow other classes to READ (get) and WRITE (set) the fields.
    // This is called "Encapsulation" — one of the 4 pillars of OOP.

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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
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

    // ---- toString() ----
    // This is useful for debugging — when you print a Task object,
    // it shows the field values instead of a cryptic memory address.
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", userId='" + userId + '\'' +
                '}';
    }
}
