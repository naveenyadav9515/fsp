package com.enterprise.taskmanager.exception;

/*
 * ============================================================
 * GLOBAL EXCEPTION HANDLER — The App's Error Safety Net
 * ============================================================
 *
 * WHAT IS THIS?
 * This class catches ALL exceptions thrown anywhere in your app
 * and converts them into clean, consistent JSON error responses.
 *
 * WITHOUT this class:
 *   - Errors would show ugly HTML pages or stack traces
 *   - Each controller would need its own error handling (messy!)
 *   - The frontend wouldn't know what format errors come in
 *
 * WITH this class:
 *   - ALL errors are caught in ONE place
 *   - ALL errors return the SAME JSON format
 *   - The frontend can easily parse and display errors
 *
 * HOW IT WORKS:
 *   @ControllerAdvice = "Apply this to ALL controllers"
 *   @ExceptionHandler = "When THIS type of exception is thrown, call THIS method"
 *
 * ERROR RESPONSE FORMAT (what the frontend receives):
 *   {
 *     "timestamp": "2024-03-19T12:00:00",
 *     "status": 404,
 *     "error": "Not Found",
 *     "message": "Task not found with ID: abc123",
 *     "path": "/api/tasks/abc123"
 *   }
 *
 * ============================================================
 */

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Create a logger (used to print messages to the console for debugging)
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * HANDLER 1: Validation Errors
     * ─────────────────────────────
     * Triggered when: @Valid fails on a DTO (e.g., title is blank)
     * HTTP Status: 400 Bad Request
     *
     * Example response:
     * {
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "details": {
     *     "title": "Title is required!",
     *     "priority": "Priority is required!"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        logger.warn("Validation error on path: {}", request.getRequestURI());

        // Collect all validation errors into a map
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            logger.debug("  Field '{}': {}", error.getField(), error.getDefaultMessage());
        }

        // Build the error response
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());    // 400
        body.put("error", "Validation Failed");
        body.put("path", request.getRequestURI());
        body.put("details", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /*
     * HANDLER 2: Resource Not Found
     * ─────────────────────────────
     * Triggered when: we throw ResourceNotFoundException
     * HTTP Status: 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("Resource not found: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());      // 404
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /*
     * HANDLER 3: Bad Request (e.g., duplicate username)
     * ──────────────────────────────────────────────────
     * Triggered when: we throw IllegalArgumentException
     * HTTP Status: 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warn("Bad request: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());    // 400
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /*
     * HANDLER 4: Catch-All (Any unexpected error)
     * ────────────────────────────────────────────
     * This is the FALLBACK. If an exception doesn't match
     * any of the handlers above, it lands here.
     * HTTP Status: 500 Internal Server Error
     *
     * IMPORTANT: We log the full stack trace for debugging,
     * but we DON'T send it to the client (security risk!).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllOtherErrors(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error on path: {}", request.getRequestURI(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());  // 500
        body.put("error", "Internal Server Error");
        body.put("message", "Something went wrong. Please try again later.");
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
