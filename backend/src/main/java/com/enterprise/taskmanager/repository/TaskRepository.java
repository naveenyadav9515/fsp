package com.enterprise.taskmanager.repository;

/*
 * ============================================================
 * TASK REPOSITORY — The Database Access Layer for Tasks
 * ============================================================
 *
 * WHAT IS A REPOSITORY?
 * A Repository is like a "helper" that talks to the database FOR you.
 * Instead of writing raw database queries, you just call methods like:
 *   - taskRepository.save(task)       → INSERT or UPDATE
 *   - taskRepository.findAll()        → SELECT * FROM tasks
 *   - taskRepository.findById("123")  → SELECT * WHERE id = "123"
 *   - taskRepository.delete(task)     → DELETE FROM tasks WHERE ...
 *
 * HOW DOES IT WORK?
 * We just create an INTERFACE (not a class!) that extends MongoRepository.
 * Spring Boot AUTOMATICALLY creates the implementation at runtime.
 * You get all basic CRUD operations for FREE!
 *
 * MongoRepository<Task, String> means:
 *   - Task   = the type of document we're storing
 *   - String = the type of the ID field (@Id)
 *
 * CUSTOM QUERIES:
 * You can add custom methods by just writing method names!
 * Spring reads the method name and creates the query automatically.
 * This is called "Derived Query Methods".
 *
 * ============================================================
 */

import com.enterprise.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository tells Spring: "This is a database access component"
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    /*
     * CUSTOM QUERY METHODS (Spring creates the SQL/query automatically!)
     *
     * How Spring reads the method name:
     *   findByStatus → "Find all tasks WHERE status = ?"
     *   findByUserId → "Find all tasks WHERE userId = ?"
     *   findByUserIdAndStatus → "Find tasks WHERE userId = ? AND status = ?"
     *
     * The parameter names match the fields in the Task model.
     */

    // Find all tasks for a specific user (returns a Page for pagination)
    Page<Task> findByUserId(String userId, Pageable pageable);

    // Find all tasks with a specific status
    List<Task> findByStatus(Task.TaskStatus status);

    // Find all tasks for a user with a specific status
    List<Task> findByUserIdAndStatus(String userId, Task.TaskStatus status);
}
