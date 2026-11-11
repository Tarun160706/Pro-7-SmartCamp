package com.campus.service.dto;

import com.campus.service.entity.Category;
import com.campus.service.entity.Priority;
import com.campus.service.entity.RequestStatus;
import java.time.LocalDateTime;

/**
 * ============================================================
 * DTO: ServiceRequestResponseDTO
 * ============================================================
 *
 * Used when the SERVER SENDS data back to the client.
 *
 * This DTO includes ALL information about a service request:
 * - Request details (title, description, status, etc.)
 * - Student info (id, name, email) - NOT the full Student entity
 * - Technician info (id, name) - NOT the full Technician entity
 * - Timestamps (createdAt, updatedAt)
 *
 * WHY A SEPARATE RESPONSE DTO?
 * â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
 * Compare what the client SENDS vs what they RECEIVE:
 *
 * Client SENDS (ServiceRequestDTO):      Server RETURNS (ServiceRequestResponseDTO):
 * â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”                    â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
 * â”‚ title            â”‚                    â”‚ id                       â”‚
 * â”‚ description      â”‚                    â”‚ title                    â”‚
 * â”‚ category         â”‚                    â”‚ description              â”‚
 * â”‚ priority         â”‚                    â”‚ category                 â”‚
 * â”‚ location         â”‚                    â”‚ priority                 â”‚
 * â”‚ studentId        â”‚                    â”‚ status                   â”‚
 * â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜                    â”‚ location                 â”‚
 *    (6 fields)                           â”‚ createdAt                â”‚
 *                                         â”‚ updatedAt                â”‚
 *                                         â”‚ studentId                â”‚
 *                                         â”‚ studentName              â”‚
 *                                         â”‚ studentEmail             â”‚
 *                                         â”‚ technicianId             â”‚
 *                                         â”‚ technicianName           â”‚
 *                                         â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
 *                                            (14 fields)
 *
 * Notice: The response has student NAME and EMAIL (not the full entity),
 * and technician NAME (not the full entity). This is FLATTENING -
 * converting nested objects into flat fields.
 *
 * INTERVIEW Q: "What is DTO flattening?"
 * A: "Instead of nesting full entity objects in the response (which
 *     causes infinite recursion), I extract only the needed fields
 *     into flat DTO fields. For example, instead of including the
 *     full Student object, I include studentId, studentName, and
 *     studentEmail as separate fields."
 */
public class ServiceRequestResponseDTO {

    // ==========================================
    // REQUEST DETAILS
    // ==========================================

    private Long id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private RequestStatus status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==========================================
    // FLATTENED STUDENT INFO
    // ==========================================
    // Instead of: private Student student; (causes recursion)
    // We use flat fields:

    private Long studentId;
    private String studentName;
    private String studentEmail;

    // ==========================================
    // FLATTENED TECHNICIAN INFO
    // ==========================================
    // Can be null if no technician is assigned yet (status = PENDING)

    private Long technicianId;
    private String technicianName;
    private String workerNotes;
    private String adminRemarks;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public ServiceRequestResponseDTO() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }
    public String getWorkerNotes() {
        return workerNotes;
    }

    public void setWorkerNotes(String workerNotes) {
        this.workerNotes = workerNotes;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }
}