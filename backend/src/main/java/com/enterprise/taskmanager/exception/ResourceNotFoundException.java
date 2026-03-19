package com.enterprise.taskmanager.exception;

/*
 * ============================================================
 * RESOURCE NOT FOUND EXCEPTION — Custom 404 Error
 * ============================================================
 *
 * WHAT IS A CUSTOM EXCEPTION?
 * Java lets you create your own Exception classes.
 * This is useful when you want to throw specific errors
 * that mean something to YOUR application.
 *
 * For example:
 *   throw new ResourceNotFoundException("Task not found with ID: 123")
 *
 * This exception is then caught by our GlobalExceptionHandler,
 * which turns it into a clean JSON error response.
 *
 * WHY "extends RuntimeException"?
 * - RuntimeException = "unchecked" exception (you don't HAVE to catch it)
 * - Exception = "checked" exception (you MUST catch it or declare it)
 *
 * We use RuntimeException because Spring's @ExceptionHandler
 * will catch it for us in the GlobalExceptionHandler.
 *
 * @ResponseStatus tells Spring: "When this exception is thrown,
 * send back a 404 Not Found HTTP status code"
 *
 * ============================================================
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor: takes a message like "Task not found with ID: abc123"
    public ResourceNotFoundException(String message) {
        super(message);  // Pass the message to the parent RuntimeException class
    }
}
