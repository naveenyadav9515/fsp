package com.enterprise.taskmanager.config;

/*
 * ============================================================
 * CACHE CONFIGURATION — Speed Up Your App!
 * ============================================================
 *
 * WHAT IS CACHING?
 * Caching = storing frequently used data in MEMORY (RAM)
 * so you don't have to query the database every time.
 *
 * EXAMPLE:
 *   Without caching: Every call to getTaskById hits the database
 *   With caching:    First call hits DB → stores result in memory
 *                    Second call → returns from memory (MUCH faster!)
 *
 * HOW SPRING CACHE WORKS:
 *   @Cacheable("tasks")    → "Before running this method, check if the
 *                             result is already cached. If yes, return
 *                             the cached version. If no, run the method
 *                             and store the result in cache."
 *
 *   @CacheEvict("tasks")   → "Remove this data from the cache"
 *                             (used when data changes — create/update/delete)
 *
 * CACHE TYPES:
 *   - ConcurrentMapCacheManager = Simple in-memory cache (good for learning)
 *   - Redis = External cache server (good for production/distributed apps)
 *   - Caffeine = High-performance in-memory cache
 *
 * We use ConcurrentMapCacheManager because it's the SIMPLEST to understand.
 *
 * ============================================================
 */

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @EnableCaching = "Turn ON Spring's caching system"
@Configuration
@EnableCaching
public class CacheConfig {

    /*
     * Create a CacheManager that manages our caches.
     *
     * We define a cache called "tasks" — this is the name we'll
     * reference in @Cacheable("tasks") annotations in our service.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tasks");
    }
}
