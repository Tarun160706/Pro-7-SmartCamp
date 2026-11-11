package com.campus.service.dto;

import com.campus.service.entity.Role;

public class AuthResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private Role role;
    private Long studentId;
    private Long technicianId;
    private String message;

    public AuthResponseDTO() {}

    public AuthResponseDTO(Long userId, String name, String email, Role role, Long studentId, Long technicianId, String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.studentId = studentId;
        this.technicianId = technicianId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}