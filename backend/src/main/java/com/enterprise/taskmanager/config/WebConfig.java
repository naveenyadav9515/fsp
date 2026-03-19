package com.enterprise.taskmanager.config;

/*
 * ============================================================
 * WEB CONFIGURATION — CORS (Cross-Origin Resource Sharing)
 * ============================================================
 *
 * WHAT IS CORS?
 * CORS is a browser security feature that blocks web pages from
 * making requests to a different domain/port.
 *
 * THE PROBLEM:
 *   Frontend runs on: http://localhost:4200 (Angular)
 *   Backend runs on:  http://localhost:8080 (Spring Boot)
 *
 *   These are DIFFERENT ORIGINS (different ports), so the browser
 *   BLOCKS the request by default! This is a security measure.
 *
 * THE SOLUTION:
 *   We configure the backend to say: "Hey browser, it's OK to
 *   accept requests from http://localhost:4200"
 *
 * CORS SETTINGS EXPLAINED:
 *   allowedOrigins  → Which websites can call our API
 *   allowedMethods  → Which HTTP methods are allowed (GET, POST, etc.)
 *   allowedHeaders  → Which headers the client can send
 *   allowCredentials → Whether cookies/tokens can be sent
 *
 * ============================================================
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebMvcConfigurer = An interface that lets us customize Spring MVC behavior
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**")                    // Apply CORS to all /api/ endpoints
            .allowedOrigins("http://localhost:4200")   // Allow Angular frontend
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // Allow these HTTP methods
            .allowedHeaders("*")                       // Allow all headers (including Authorization)
            .allowCredentials(true);                    // Allow cookies/tokens
    }
}
