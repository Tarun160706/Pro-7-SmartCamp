package com.campus.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ============================================================
 * DTO: TechnicianDTO
 * ============================================================
 *
 * Used for creating and returning technician data through the API.
 */
public class TechnicianDTO {

    private Long id;

    @NotBlank(message = "Technician name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public TechnicianDTO() {
    }

    public TechnicianDTO(Long id, String name, String email, String specialization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}