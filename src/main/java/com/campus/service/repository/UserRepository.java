package com.campus.service.repository;

import com.campus.service.entity.Role;
import com.campus.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORY: UserRepository
 * ============================================================
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    Optional<User> findByStudentId(Long studentId);

    Optional<User> findByTechnicianId(Long technicianId);
}