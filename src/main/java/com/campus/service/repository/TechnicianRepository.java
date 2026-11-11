package com.campus.service.repository;

import com.campus.service.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORY: TechnicianRepository
 * ============================================================
 *
 * Handles all database operations for the Technician entity.
 *
 * Inherits from JpaRepository, so we automatically get:
 * - save(), findById(), findAll(), deleteById(), count(), existsById()
 *
 * Custom methods added:
 * - findByEmail()    : Find technician by email
 * - existsByEmail()  : Check if email already exists
 */
@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    /**
     * Find a technician by email.
     * Generated SQL: SELECT * FROM technicians WHERE email = ?
     */
    Optional<Technician> findByEmail(String email);

    /**
     * Check if a technician with this email already exists.
     * Generated SQL: SELECT EXISTS(SELECT 1 FROM technicians WHERE email = ?)
     */
    boolean existsByEmail(String email);
}