package com.campus.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTITY: ServiceRequest
 * ============================================================
 *
 * This is the CORE entity of the application.
 * It represents a campus service/maintenance request created by a student.
 *
 * Examples:
 * - "Projector not working in Room 301"
 * - "Wi-Fi down in Library 2nd Floor"
 * - "Broken chair in Lab 5"
 *
 * RELATIONSHIPS:
 * - Many ServiceRequests can belong to ONE Student  (N:1)
 * - Many ServiceRequests can be assigned to ONE Technician (N:1)
 *
 *   Student (1) ---- (N) ServiceRequest (N) ---- (1) Technician
 *
 * DATABASE TABLE CREATED:
 * -----------------------
 * CREATE TABLE service_requests (
 *     id              BIGSERIAL PRIMARY KEY,
 *     title           VARCHAR(200) NOT NULL,
 *     description     TEXT NOT NULL,
 *     category        VARCHAR(50) NOT NULL,
 *     priority        VARCHAR(50) NOT NULL,
 *     status          VARCHAR(50) NOT NULL,
 *     location        VARCHAR(200) NOT NULL,
 *     created_at      TIMESTAMP NOT NULL,
 *     updated_at      TIMESTAMP NOT NULL,
 *     student_id      BIGINT REFERENCES students(id),
 *     technician_id   BIGINT REFERENCES technicians(id)
 * );
 *
 * NEW JPA CONCEPTS IN THIS CLASS:
 * --------------------------------
 * @ManyToOne   - Many requests belong to one student/technician
 * @JoinColumn  - Specifies the foreign key column name
 * @Enumerated  - Maps Java enums to database columns
 * @PrePersist  - Lifecycle callback: runs BEFORE first save (INSERT)
 * @PreUpdate   - Lifecycle callback: runs BEFORE every update (UPDATE)
 *
 * INTERVIEW QUESTIONS:
 * --------------------
 * Q: "What is @ManyToOne?"
 * A: "It defines a many-to-one relationship. Many service requests
 *     can belong to one student. In the database, this creates a
 *     foreign key column (student_id) in the service_requests table
 *     that references the students table."
 *
 * Q: "What is @JoinColumn?"
 * A: "It specifies the name of the foreign key column in the database.
 *     Without @JoinColumn, JPA would auto-generate a column name.
 *     Using @JoinColumn(name='student_id') makes it explicit and readable."
 *
 * Q: "Why use @Enumerated(EnumType.STRING) instead of EnumType.ORDINAL?"
 * A: "EnumType.STRING stores the enum name as a string (e.g., 'PENDING').
 *     EnumType.ORDINAL stores the position as an integer (0, 1, 2...).
 *     If we use ORDINAL and later add a new enum value in the middle,
 *     all existing data would shift and become incorrect. STRING is safer."
 *
 * Q: "What are @PrePersist and @PreUpdate?"
 * A: "They are JPA lifecycle callbacks. @PrePersist runs automatically
 *     before an entity is first saved to the database. @PreUpdate runs
 *     before an entity is updated. I use them to automatically set
 *     createdAt and updatedAt timestamps without manual code."
 *
 * Q: "Why use LocalDateTime instead of Date?"
 * A: "LocalDateTime is from the modern java.time API (Java 8+).
 *     It is immutable, thread-safe, and easier to work with than
 *     the old java.util.Date which has many design problems."
 */
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    // ==========================================
    // FIELDS
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A short title summarizing the issue.
     * Example: "Projector not working in Room 301"
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Detailed description of the issue.
     * Using columnDefinition = "TEXT" allows unlimited text length
     * (unlike VARCHAR which has a limit).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * The type of issue (ELECTRICAL, INTERNET, COMPUTER, etc.)
     *
     * @Enumerated(EnumType.STRING)
     * - Stores the enum as a readable string in the database.
     * - The database column will contain values like "ELECTRICAL", "INTERNET"
     * - DO NOT use EnumType.ORDINAL (stores 0, 1, 2 which breaks if enum order changes)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    /**
     * How urgent the issue is (LOW, MEDIUM, HIGH, CRITICAL).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Priority priority;

    /**
     * Current status of the request.
     * Follows the lifecycle: PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RequestStatus status;

    /**
     * Where the issue is located on campus.
     * Example: "Room 301, Block A", "Computer Lab 5, 2nd Floor"
     */
    @Column(nullable = false, length = 200)
    private String location;

    /**
     * When the request was first created.
     * This is automatically set by @PrePersist (see onCreate method below).
     * updatable = false means this value cannot be changed after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * When the request was last updated.
     * This is automatically set by @PrePersist and @PreUpdate.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==========================================
    // RELATIONSHIPS
    // ==========================================

    /**
     * The student who created this request.
     *
     * @ManyToOne = Many service requests can belong to ONE student.
     *
     * @JoinColumn(name = "student_id")
     * - Creates a foreign key column called "student_id" in the service_requests table.
     * - This column references the "id" column in the students table.
     *
     * This is the OWNING SIDE of the Student-ServiceRequest relationship.
     * The owning side is always the "many" side (the one with the foreign key).
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    /**
     * The technician assigned to handle this request.
     *
     * @ManyToOne = Many service requests can be assigned to ONE technician.
     *
     * This can be NULL initially (when status is PENDING, no technician is assigned yet).
     * A technician is assigned when the status changes to ASSIGNED.
     */
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "worker_notes", columnDefinition = "TEXT")
    private String workerNotes;

    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;

    // ==========================================
    // JPA LIFECYCLE CALLBACKS
    // ==========================================

    /**
     * Automatically called BEFORE the entity is first saved (INSERT).
     *
     * Sets:
     * - createdAt = current time
     * - updatedAt = current time
     * - status = PENDING (default status for new requests)
     *
     * This is like a "constructor" that runs at database-save time.
     * It ensures these fields are always set correctly without
     * relying on the developer to remember to set them manually.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
    }

    /**
     * Automatically called BEFORE every update (UPDATE).
     *
     * Updates the updatedAt timestamp to the current time.
     * This way, we always know when a request was last modified.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    /**
     * Default constructor - Required by JPA.
     */
    public ServiceRequest() {
    }

    /**
     * Parameterized constructor for creating a new service request.
     *
     * Note: id, createdAt, updatedAt, status are NOT included because:
     * - id: auto-generated by database
     * - createdAt/updatedAt: auto-set by @PrePersist
     * - status: defaults to PENDING via @PrePersist
     * - technician: not assigned at creation time
     */
    public ServiceRequest(String title, String description, Category category,
                          Priority priority, String location, Student student) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.location = location;
        this.student = student;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    // ==========================================
    // toString()
    // ==========================================

    /**
     * Note: We print student.getId() and technician.getId() instead of
     * the full objects to avoid infinite recursion:
     * ServiceRequest.toString() -> Student.toString() -> ServiceRequest.toString() -> ...
     */
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

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", priority=" + priority +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", studentId=" + (student != null ? student.getId() : null) +
                ", technicianId=" + (technician != null ? technician.getId() : null) +
                '}';
    }
}
