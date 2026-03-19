package com.enterprise.taskmanager.repository;

/*
 * ============================================================
 * USER REPOSITORY — The Database Access Layer for Users
 * ============================================================
 *
 * Same concept as TaskRepository, but for User documents.
 * We add a custom method to find a user by their username
 * (needed for login functionality).
 *
 * ============================================================
 */

import com.enterprise.taskmanager.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /*
     * Find a user by their username.
     *
     * Returns Optional<User> which means:
     *   - If user exists    → Optional.of(user)    → contains the user
     *   - If user NOT found → Optional.empty()      → contains nothing
     *
     * Optional forces you to handle the "not found" case,
     * preventing NullPointerException errors!
     */
    Optional<User> findByUsername(String username);

    /*
     * Check if a username already exists in the database.
     * Returns true if a user with this username exists, false otherwise.
     * Useful for registration: "Sorry, this username is taken!"
     */
    boolean existsByUsername(String username);
}
