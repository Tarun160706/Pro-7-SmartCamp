package com.campus.service.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminResolveDTO {

    @NotBlank(message = "Admin resolution remarks to student cannot be blank")
    private String adminRemarks;

    public AdminResolveDTO() {}

    public AdminResolveDTO(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }
}