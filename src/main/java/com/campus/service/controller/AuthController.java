package com.campus.service.controller;

import com.campus.service.dto.AuthResponseDTO;
import com.campus.service.dto.LoginRequestDTO;
import com.campus.service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * CONTROLLER: AuthController
 * ============================================================
 *
 * Role-Based Login & Identity Verification endpoint.
 * Supports STUDENT, WORKER, and ADMIN logins.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Verifies email + password against BCrypt hashed credentials in database.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        AuthResponseDTO response = authService.authenticate(loginDTO);
        return ResponseEntity.ok(response);
    }
}