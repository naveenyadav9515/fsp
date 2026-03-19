package com.enterprise.taskmanager.service;

/*
 * ============================================================
 * TASK SERVICE — Business Logic for Task Management
 * ============================================================
 *
 * This is the "brain" of the Task Manager.
 * All business rules and data processing happen HERE.
 *
 * RESPONSIBILITIES:
 *   - Create a new task
 *   - Get all tasks (with PAGINATION)
 *   - Get a single task by ID
 *   - Update a task (full update)
 *   - Update just the task status
 *   - Delete a task
 *   - Convert between Model and DTO
 *
 * CACHING:
 *   We use Spring Cache to avoid hitting the database every time
 *   someone requests a task by ID. See comments on each method!
 *
 * ============================================================
 */

import com.enterprise.taskmanager.dto.TaskRequestDto;
import com.enterprise.taskmanager.dto.TaskResponseDto;
import com.enterprise.taskmanager.exception.ResourceNotFoundException;
import com.enterprise.taskmanager.model.Task;
import com.enterprise.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    // The repository (database access) — injected by Spring
    private final TaskRepository taskRepository;

    // Constructor Injection
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ════════════════════════════════════════════════════════
    // CREATE — Add a new task to the database
    // ════════════════════════════════════════════════════════
    /*
     * @CacheEvict(value = "tasks", allEntries = true)
     * → When a new task is created, CLEAR the entire "tasks" cache
     *   because the cached list is now outdated.
     */
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDto createTask(TaskRequestDto request, String userId) {
        logger.info("Creating new task '{}' for user '{}'", request.getTitle(), userId);

        // Step 1: Create a new Task model object
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(userId);

        // Step 2: Parse the priority string into the enum
        // e.g., "HIGH" → Task.TaskPriority.HIGH
        task.setPriority(Task.TaskPriority.valueOf(request.getPriority().toUpperCase()));

        // Step 3: Set default status and timestamps
        task.setStatus(Task.TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Step 4: Save to database (MongoDB generates the ID automatically)
        Task savedTask = taskRepository.save(task);
        logger.info("Task created with ID: {}", savedTask.getId());

        // Step 5: Convert to Response DTO and return
        return convertToResponseDto(savedTask);
    }

    // ════════════════════════════════════════════════════════
    // READ ALL — Get all tasks for a user (with PAGINATION)
    // ════════════════════════════════════════════════════════
    /*
     * WHAT IS PAGINATION?
     * Instead of returning ALL tasks at once (could be thousands!),
     * we return them in "pages":
     *   Page 0: Tasks 1-10
     *   Page 1: Tasks 11-20
     *   etc.
     *
     * WHY PAGINATION?
     *   - Without: Loading 10,000 tasks → slow, uses tons of memory
     *   - With:    Loading 10 tasks per page → fast, efficient
     *
     * HOW TO USE:
     *   GET /api/tasks?page=0&size=10&sort=createdAt,desc
     *   - page = which page (starts from 0)
     *   - size = how many items per page
     *   - sort = which field to sort by, and direction (asc/desc)
     */
    public Page<TaskResponseDto> getAllTasks(String userId, Pageable pageable) {
        logger.debug("Fetching tasks for user '{}', page: {}", userId, pageable.getPageNumber());

        // findByUserId returns a Page<Task>, which contains:
        //   - The list of tasks for this page
        //   - Total number of tasks
        //   - Total number of pages
        //   - Current page number
        Page<Task> taskPage = taskRepository.findByUserId(userId, pageable);

        // Convert each Task to TaskResponseDto using the .map() method
        // .map() applies a function to every element in the page
        return taskPage.map(this::convertToResponseDto);
    }

    // ════════════════════════════════════════════════════════
    // READ ONE — Get a single task by its ID
    // ════════════════════════════════════════════════════════
    /*
     * @Cacheable(value = "tasks", key = "#id")
     * → "Before running this method, check the 'tasks' cache.
     *    If a result for this 'id' already exists, return it from cache.
     *    If not, run the method and cache the result."
     *
     * The 'key = "#id"' means: use the task ID as the cache key.
     * So each task ID has its own cached value.
     */
    @Cacheable(value = "tasks", key = "#id")
    public TaskResponseDto getTaskById(String id) {
        logger.debug("Fetching task with ID: {}", id);

        // findById returns Optional<Task>
        // orElseThrow: If the task doesn't exist, throw our custom exception
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        return convertToResponseDto(task);
    }

    // ════════════════════════════════════════════════════════
    // UPDATE — Fully update an existing task
    // ════════════════════════════════════════════════════════
    /*
     * PUT vs PATCH:
     *   PUT   = Replace the ENTIRE resource (all fields)
     *   PATCH = Update only SOME fields
     *
     * @CacheEvict → Remove this task from cache (it changed!)
     */
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDto updateTask(String id, TaskRequestDto request) {
        logger.info("Updating task ID: {}", id);

        // Step 1: Find the existing task (throws 404 if not found)
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        // Step 2: Update the fields
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setPriority(Task.TaskPriority.valueOf(request.getPriority().toUpperCase()));
        existingTask.setUpdatedAt(LocalDateTime.now());

        // Step 3: Save the updated task
        Task savedTask = taskRepository.save(existingTask);
        logger.info("Task ID: {} updated successfully", id);

        return convertToResponseDto(savedTask);
    }

    // ════════════════════════════════════════════════════════
    // UPDATE STATUS — Change only the task status (PATCH)
    // ════════════════════════════════════════════════════════
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDto updateTaskStatus(String id, Task.TaskStatus newStatus) {
        logger.info("Updating status for task ID: {} to {}", id, newStatus);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        existingTask.setStatus(newStatus);
        existingTask.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(existingTask);
        return convertToResponseDto(savedTask);
    }

    // ════════════════════════════════════════════════════════
    // DELETE — Remove a task from the database
    // ════════════════════════════════════════════════════════
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(String id) {
        logger.info("Deleting task with ID: {}", id);

        // Check if the task exists before deleting
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        taskRepository.delete(existingTask);
        logger.info("Task ID: {} deleted successfully", id);
    }

    // ════════════════════════════════════════════════════════
    // HELPER: Convert Task Model → TaskResponseDto
    // ════════════════════════════════════════════════════════
    /*
     * This method converts a database model (Task) into a DTO
     * that we send back to the client.
     *
     * WHY? Because we might not want to expose ALL model fields
     * to the client (e.g., internal audit fields).
     */
    private TaskResponseDto convertToResponseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setUserId(task.getUserId());
        return dto;
    }
}
