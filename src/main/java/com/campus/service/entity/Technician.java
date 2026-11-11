package com.campus.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ENTITY: Technician
 * ============================================================
 *
 * This class represents the TECHNICIANS table in the database.
 * A technician is a staff member who is assigned to fix reported issues.
 *
 * DATABASE TABLE CREATED:
 * -----------------------
 * CREATE TABLE technicians (
 *     id              BIGSERIAL PRIMARY KEY,
 *     name            VARCHAR(100) NOT NULL,
 *     email           VARCHAR(100) NOT NULL UNIQUE,
 *     specialization  VARCHAR(100) NOT NULL
 * );
 *
 * RELATIONSHIP:
 * - One Technician can be assigned to MANY ServiceRequests.
 * - Technician (1) ---- (N) ServiceRequest
 *
 * INTERVIEW QUESTIONS:
 * --------------------
 * Q: "How do you model a one-to-many relationship in JPA?"
 * A: "The 'many' side (ServiceRequest) uses @ManyToOne with @JoinColumn
 *     to hold the foreign key. The 'one' side (Technician) uses
 *     @OneToMany(mappedBy='technician') to reference the relationship
 *     without creating a duplicate foreign key column."
 *
 * Q: "Why initialize the List with new ArrayList<>()?"
 * A: "To avoid NullPointerException. If we don't initialize it,
 *     the list would be null by default. When we try to call
 *     technician.getAssignedRequests().add(request), it would throw
 *     NullPointerException because you can't call .add() on null."
 */
@Entity
@Table(name = "technicians")
public class Technician {

    // ==========================================
    // FIELDS
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * The technician's area of expertise.
     * Examples: "Electrical", "IT/Networking", "Plumbing", "General Maintenance"
     *
     * This helps administrators assign the RIGHT technician to the RIGHT issue.
     * e.g., An electrical issue should go to a technician with
     * specialization = "Electrical", not "IT/Networking".
     */
    @Column(nullable = false, length = 100)
    private String specialization;

    /**
     * List of service requests assigned to this technician.
     *
     * @OneToMany(mappedBy = "technician")
     * - This is the INVERSE (non-owning) side of the relationship.
     * - The ServiceRequest entity is the OWNING side (has the foreign key).
     * - mappedBy = "technician" refers to the field name in ServiceRequest class.
     */
    @OneToMany(mappedBy = "technician")
    private List<ServiceRequest> assignedRequests = new ArrayList<>();

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    /**
     * Default constructor - Required by JPA.
     */
    public Technician() {
    }

    /**
     * Parameterized constructor for convenient object creation.
     */
    public Technician(String name, String email, String specialization) {
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

    public List<ServiceRequest> getAssignedRequests() {
        return assignedRequests;
    }

    public void setAssignedRequests(List<ServiceRequest> assignedRequests) {
        this.assignedRequests = assignedRequests;
    }

    // ==========================================
    // toString()
    // ==========================================

    @Override
    public String toString() {
        return "Technician{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
