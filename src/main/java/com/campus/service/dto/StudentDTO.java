package com.campus.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ============================================================
 * DTO: StudentDTO
 * ============================================================
 *
 * A DTO (Data Transfer Object) defines what data the API accepts
 * and returns. It acts as a CONTRACT between the client and server.
 *
 * WHY USE DTOs INSTEAD OF ENTITIES DIRECTLY?
 * ─────────────────────────────────────────
 * 1. SECURITY:
 *    - Entities may contain sensitive fields you don't want to expose.
 *    - Example: If Student had a 'password' field, sending the entity
 *      directly would leak the password in the JSON response.
 *
 * 2. DECOUPLING:
 *    - The API contract (DTO) is independent of the database schema (Entity).
 *    - If you rename a database column, the API doesn't change.
 *    - Clients don't break when you modify the entity.
 *
 * 3. VALIDATION:
 *    - DTOs carry validation annotations (@NotBlank, @Email).
 *    - We validate incoming data BEFORE it reaches the service/database.
 *    - Entities should NOT contain validation annotations (separation of concerns).
 *
 * 4. CONTROL:
 *    - You choose exactly which fields the client can send/receive.
 *    - Clients cannot set 'id' or 'createdAt' through the DTO.
 *    - You can have different DTOs for CREATE vs UPDATE vs RESPONSE.
 *
 * 5. AVOIDING INFINITE RECURSION:
 *    - Entities have bidirectional relationships (Student -> ServiceRequests -> Student).
 *    - Serializing an entity to JSON would cause infinite recursion.
 *    - DTOs break this cycle by containing only the data we want.
 *
 * JAVA CONCEPTS:
 * - Encapsulation: private fields + public getters/setters
 * - Validation annotations from Jakarta Bean Validation
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "What is a DTO and why do you use it?"
 * A: "A DTO is a Data Transfer Object - a plain Java class that carries
 *     data between the client and server. I use DTOs to decouple the
 *     API contract from the database schema, validate input, control
 *     which fields are exposed, and avoid infinite recursion in
 *     bidirectional entity relationships."
 *
 * Q: "Why not just use the Entity class for the API?"
 * A: "Three main reasons: 1) Security - entities might expose internal
 *     fields like passwords. 2) Infinite recursion - bidirectional
 *     relationships cause Jackson to loop infinitely. 3) Decoupling -
 *     changing the database schema shouldn't break the API contract."
 */
public class StudentDTO {

    /**
     * Student ID - only used in RESPONSES, not in create requests.
     * When creating a student, this is null (database generates it).
     * When returning a student, this contains the generated ID.
     */
    private Long id;

    /**
     * @NotBlank = The field cannot be null, empty (""), or just whitespace ("   ").
     *
     * Difference between @NotNull, @NotEmpty, @NotBlank:
     * ┌────────────┬──────┬──────┬───────────┐
     * │ Annotation │ null │ ""   │ "   "     │
     * ├────────────┼──────┼──────┼───────────┤
     * │ @NotNull   │  ✗   │  ✓   │  ✓        │
     * │ @NotEmpty  │  ✗   │  ✗   │  ✓        │
     * │ @NotBlank  │  ✗   │  ✗   │  ✗        │
     * └────────────┴──────┴──────┴───────────┘
     * @NotBlank is the strictest - perfect for names and titles.
     */
    @NotBlank(message = "Student name is required")
    private String name;

    /**
     * @Email = Validates that the value follows email format.
     * @NotBlank = Also ensures it's not empty.
     *
     * Both annotations work together:
     * - @NotBlank catches null/""/whitespace
     * - @Email catches invalid formats like "abc" or "abc@"
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    /**
     * @NotNull = Year cannot be null (but can be 0 with @Min/@Max).
     * @Min(1) = Minimum value is 1 (1st year).
     * @Max(5) = Maximum value is 5 (for integrated courses).
     *
     * Note: We use @NotNull instead of @NotBlank because year is an
     * Integer (number), not a String. @NotBlank only works on Strings.
     */
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 5, message = "Year must be at most 5")
    private Integer year;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    public StudentDTO() {
    }

    public StudentDTO(Long id, String name, String email, String department, Integer year) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}