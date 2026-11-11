package com.campus.service.repository;

import com.campus.service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORY: StudentRepository
 * ============================================================
 *
 * This interface handles all database operations for the Student entity.
 *
 * KEY CONCEPTS:
 *
 * 1. INTERFACE (not a class!)
 *    - A repository is an INTERFACE, not a class.
 *    - We do NOT write the implementation. Spring Data JPA automatically
 *      creates an implementation class at runtime using a proxy.
 *    - This is one of the most powerful features of Spring Data JPA.
 *
 * 2. JpaRepository<Student, Long>
 *    - Student = The entity type this repository manages.
 *    - Long = The type of the entity's primary key (@Id field).
 *    - By extending JpaRepository, we get ALL these methods for FREE:
 *
 *    CRUD Methods (auto-provided):
 *    ┌──────────────────────────────────────────────────────────────────┐
 *    │ save(entity)            - INSERT or UPDATE a student            │
 *    │ findById(id)            - SELECT * WHERE id = ?                 │
 *    │ findAll()               - SELECT * (get all students)           │
 *    │ findAll(Pageable)       - SELECT * with pagination              │
 *    │ deleteById(id)          - DELETE WHERE id = ?                   │
 *    │ count()                 - SELECT COUNT(*)                       │
 *    │ existsById(id)          - SELECT EXISTS(id)                     │
 *    └──────────────────────────────────────────────────────────────────┘
 *
 * 3. @Repository
 *    - Marks this as a Spring-managed component (a bean).
 *    - Spring will create an instance and manage its lifecycle.
 *    - Also translates database-specific exceptions into Spring's
 *      DataAccessException hierarchy.
 *    - Note: @Repository is actually optional when extending JpaRepository
 *      (Spring auto-detects it), but adding it makes the intent explicit.
 *
 * 4. CUSTOM QUERY METHODS (Method Naming Convention)
 *    - Spring Data JPA generates SQL from method names!
 *    - findByEmail(email) becomes: SELECT * FROM students WHERE email = ?
 *    - existsByEmail(email) becomes: SELECT EXISTS(... WHERE email = ?)
 *    - No SQL writing needed!
 *
 * JAVA CONCEPTS:
 * - Interface: A contract that defines method signatures without implementation.
 * - Generics: JpaRepository<Student, Long> uses generics for type safety.
 * - Optional: A container that may or may not contain a value (avoids null).
 * - Inheritance: We inherit all CRUD methods from JpaRepository.
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "What is JpaRepository?"
 * A: "JpaRepository is a Spring Data JPA interface that provides
 *     ready-to-use CRUD methods. By extending it with my entity type
 *     and ID type, I get methods like save(), findById(), findAll(),
 *     deleteById() without writing any implementation code."
 *
 * Q: "How does Spring create the implementation?"
 * A: "Spring Data JPA uses the Proxy design pattern. At startup,
 *     it scans for interfaces extending JpaRepository and creates
 *     proxy objects (SimpleJpaRepository) that implement all the
 *     methods. This proxy uses EntityManager internally to execute
 *     the actual database queries."
 *
 * Q: "What is Optional and why is findByEmail returning it?"
 * A: "Optional is a Java 8 container class. It either contains a value
 *     or is empty. It forces the caller to handle the 'not found' case
 *     explicitly, preventing NullPointerException. Instead of returning
 *     null, we return Optional.empty(), and the caller uses
 *     .isPresent() or .orElseThrow() to handle it safely."
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a student by their email address.
     *
     * Spring Data JPA QUERY DERIVATION:
     * Method name: findByEmail
     *   find  = SELECT query
     *   By    = WHERE clause
     *   Email = the 'email' field in Student entity
     *
     * Generated SQL: SELECT * FROM students WHERE email = ?
     *
     * Returns Optional<Student> because the student may not exist.
     */
    Optional<Student> findByEmail(String email);

    /**
     * Check if a student with the given email already exists.
     *
     * Method name: existsByEmail
     *   exists = returns boolean (true/false)
     *   By     = WHERE clause
     *   Email  = the 'email' field
     *
     * Generated SQL: SELECT EXISTS(SELECT 1 FROM students WHERE email = ?)
     *
     * Useful for validation: check if email is already registered
     * before creating a new student.
     */
    boolean existsByEmail(String email);
}