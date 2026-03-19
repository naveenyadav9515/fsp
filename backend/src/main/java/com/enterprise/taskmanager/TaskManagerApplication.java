package com.enterprise.taskmanager;

/*
 * ============================================================
 * MAIN APPLICATION CLASS — The Starting Point of Everything
 * ============================================================
 *
 * WHAT DOES THIS FILE DO?
 * This is the ENTRY POINT of your Spring Boot application.
 * When you run this class, it starts the entire application:
 *   1. Starts the embedded web server (Tomcat)
 *   2. Scans for all Spring components (@Controller, @Service, etc.)
 *   3. Sets up the database connection
 *   4. Initializes all configurations
 *   5. Your API is now ready to receive requests!
 *
 * @SpringBootApplication is a SHORTCUT for three annotations:
 *   @Configuration     → "This class contains configuration"
 *   @EnableAutoConfiguration → "Auto-configure based on dependencies"
 *   @ComponentScan     → "Scan this package for Spring components"
 *
 * WHAT IS COMPONENT SCANNING?
 * Spring looks for classes with annotations like @Controller,
 * @Service, @Repository, @Component, etc., and automatically
 * creates instances of them (this is called "Beans").
 *
 * ============================================================
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {

    /*
     * The main() method — where Java starts executing.
     *
     * SpringApplication.run() does all the heavy lifting:
     *   1. Creates the Spring Application Context (the container for all beans)
     *   2. Starts the embedded Tomcat server on port 8080
     *   3. Your app is now live at http://localhost:8080
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
        // After this line, your API is running and ready! 🚀
    }
}
