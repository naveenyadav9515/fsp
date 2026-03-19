package com.enterprise.taskmanager.controller;

/*
 * ============================================================
 * TASK CONTROLLER — CRUD API Endpoints for Tasks
 * ============================================================
 *
 * REST API DESIGN (the standard way to design APIs):
 * ──────────────────────────────────────────────────
 *
 *   HTTP Method │ URL                      │ Purpose
 *   ────────────┼──────────────────────────┼──────────────────
 *   POST        │ /api/tasks               │ CREATE a new task
 *   GET         │ /api/tasks               │ READ all tasks (paginated)
 *   GET         │ /api/tasks/{id}          │ READ one task by ID
 *   PUT         │ /api/tasks/{id}          │ UPDATE a task (full)
 *   PATCH       │ /api/tasks/{id}/status   │ UPDATE just the status
 *   DELETE      │ /api/tasks/{id}          │ DELETE a task
 *
 * HTTP STATUS CODES (the standard response codes):
 * ─────────────────────────────────────────────────
 *   200 OK         → Request successful
 *   201 Created    → Resource created successfully
 *   204 No Content → Resource deleted successfully (nothing to return)
 *   400 Bad Request→ Invalid input from the client
 *   401 Unauthorized → Not logged in
 *   404 Not Found  → Resource doesn't exist
 *   500 Server Error → Something went wrong on the server
 *
 * ============================================================
 */

import com.enterprise.taskmanager.dto.TaskRequestDto;
import com.enterprise.taskmanager.dto.TaskResponseDto;
import com.enterprise.taskmanager.model.Task;
import com.enterprise.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    // Constructor Injection
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ════════════════════════════════════════════════════════
    // POST /api/tasks — Create a new task
    // ════════════════════════════════════════════════════════
    /*
     * @PostMapping → Maps HTTP POST requests to this method
     *
     * @Valid → Validates the request body using rules in TaskRequestDto
     *
     * @RequestBody → Converts the JSON body into a TaskRequestDto object
     *
     * Authentication → Spring Security automatically provides this!
     *   It contains the current user's info (extracted from the JWT token).
     *   authentication.getName() → returns the username
     */
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskRequestDto request,
            Authentication authentication) {

        // Get the username from the JWT token
        String userId = authentication.getName();
        logger.info("POST /api/tasks — User: {}", userId);

        TaskResponseDto createdTask = taskService.createTask(request, userId);

        // Return 201 Created
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // ════════════════════════════════════════════════════════
    // GET /api/tasks — Get all tasks (with PAGINATION)
    // ════════════════════════════════════════════════════════
    /*
     * PAGINATION PARAMETERS:
     *   @RequestParam(defaultValue = "0") int page     → Which page (starts from 0)
     *   @RequestParam(defaultValue = "10") int size    → Items per page
     *   @RequestParam(defaultValue = "createdAt") String sortBy → Sort field
     *   @RequestParam(defaultValue = "desc") String direction   → Sort direction
     *
     * EXAMPLE USAGE:
     *   GET /api/tasks                           → Page 0, 10 items, sorted by date desc
     *   GET /api/tasks?page=1&size=5             → Page 1, 5 items
     *   GET /api/tasks?sortBy=title&direction=asc → Sorted by title A-Z
     *
     * (defaultValue means: if the client doesn't send this parameter, use this value)
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        String userId = authentication.getName();
        logger.info("GET /api/tasks — User: {}, Page: {}, Size: {}", userId, page, size);

        // Create a Sort object (decides the order of results)
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create a Pageable object (combines page number, size, and sort)
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TaskResponseDto> tasks = taskService.getAllTasks(userId, pageable);

        return ResponseEntity.ok(tasks);
    }

    // ════════════════════════════════════════════════════════
    // GET /api/tasks/{id} — Get a specific task by its ID
    // ════════════════════════════════════════════════════════
    /*
     * @PathVariable → Extracts the {id} from the URL
     *   Example: GET /api/tasks/abc123 → id = "abc123"
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        logger.info("GET /api/tasks/{}", id);

        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // ════════════════════════════════════════════════════════
    // PUT /api/tasks/{id} — Update an entire task
    // ════════════════════════════════════════════════════════
    /*
     * PUT = Full Update (replace ALL fields)
     * The client must send ALL required fields, even if they didn't change.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDto request) {

        logger.info("PUT /api/tasks/{}", id);

        TaskResponseDto updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    // ════════════════════════════════════════════════════════
    // PATCH /api/tasks/{id}/status — Update only the status
    // ════════════════════════════════════════════════════════
    /*
     * PATCH = Partial Update (change only specific fields)
     *
     * @RequestParam → Reads the value from the URL query string
     *   Example: PATCH /api/tasks/abc123/status?status=COMPLETED
     *            → status = "COMPLETED"
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam Task.TaskStatus status) {

        logger.info("PATCH /api/tasks/{}/status → {}", id, status);

        TaskResponseDto updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    // ════════════════════════════════════════════════════════
    // DELETE /api/tasks/{id} — Delete a task
    // ════════════════════════════════════════════════════════
    /*
     * ResponseEntity<Void> → The response has NO body (just a status code)
     * 204 No Content = "I deleted it successfully, nothing to return"
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        logger.info("DELETE /api/tasks/{}", id);

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
