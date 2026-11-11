package com.campus.service.controller;

import com.campus.service.dto.CreateUserDTO;
import com.campus.service.dto.StudentBulkImportResultDTO;
import com.campus.service.dto.StudentCredentialDTO;
import com.campus.service.entity.User;
import com.campus.service.service.AdminManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: AdminController
 * ============================================================
 *
 * Dedicated administrative operations:
 * - Creating individual student & worker accounts
 * - Bulk importing students via CSV with automatic secure password generation
 * - Exporting generated credentials CSV for distribution
 * - Viewing all registered system accounts
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminManagementService adminService;

    public AdminController(AdminManagementService adminService) {
        this.adminService = adminService;
    }

    /**
     * POST /api/admin/create-student
     * Admin manually creates a single student account.
     */
    @PostMapping("/create-student")
    public ResponseEntity<StudentCredentialDTO> createStudent(@Valid @RequestBody CreateUserDTO dto) {
        StudentCredentialDTO cred = adminService.createStudentAccount(dto);
        return new ResponseEntity<>(cred, HttpStatus.CREATED);
    }

    /**
     * POST /api/admin/create-worker
     * Admin manually creates a single campus worker (technician) account.
     */
    @PostMapping("/create-worker")
    public ResponseEntity<User> createWorker(@Valid @RequestBody CreateUserDTO dto) {
        User workerUser = adminService.createWorkerAccount(dto);
        return new ResponseEntity<>(workerUser, HttpStatus.CREATED);
    }

    /**
     * POST /api/admin/bulk-upload-students
     * Bulk upload students by passing CSV content.
     * Generates a random secure password for each student.
     */
    @PostMapping("/bulk-upload-students")
    public ResponseEntity<StudentBulkImportResultDTO> bulkUploadStudents(@RequestBody String csvContent) {
        StudentBulkImportResultDTO result = adminService.bulkImportStudents(csvContent);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/export-credentials
     * Export all created student credentials as a downloadable CSV.
     */
    @GetMapping(value = "/export-credentials", produces = "text/csv")
    public ResponseEntity<String> exportCredentials() {
        String csvData = adminService.exportCredentialsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student_credentials.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    /**
     * GET /api/admin/cached-credentials
     * Get credentials as JSON list for the UI table.
     */
    @GetMapping("/cached-credentials")
    public ResponseEntity<List<StudentCredentialDTO>> getCachedCredentials() {
        return ResponseEntity.ok(adminService.getCachedCredentials());
    }

    /**
     * GET /api/admin/users
     * View all user accounts and their assigned roles.
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}