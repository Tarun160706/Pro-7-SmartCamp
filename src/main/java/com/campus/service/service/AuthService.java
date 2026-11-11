package com.campus.service.service;

import com.campus.service.dto.AuthResponseDTO;
import com.campus.service.dto.LoginRequestDTO;
import com.campus.service.entity.User;
import com.campus.service.exception.ResourceNotFoundException;
import com.campus.service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * SERVICE: AuthService
 * ============================================================
 *
 * Handles authentication and role-based credential verification
 * using BCrypt salted password hashing.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponseDTO authenticate(LoginRequestDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + loginDTO.getEmail()));

        // Verify password against BCrypt hash
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password credentials");
        }

        return new AuthResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStudentId(),
                user.getTechnicianId(),
                "Login successful as " + user.getRole().name()
        );
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}