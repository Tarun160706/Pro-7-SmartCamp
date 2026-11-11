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
 * ENTITY: Student
 * ============================================================
 *
 * This class represents the STUDENT table in the database.
 * Each instance of this class = one row in the student table.
 *
 * JAVA CONCEPTS USED:
 * -------------------
 * 1. CLASS & OBJECTS
 *    - Student is a class (blueprint).
 *    - Each student record is an object (instance).
 *
 * 2. ENCAPSULATION
 *    - Fields are private (cannot be accessed directly from outside).
 *    - Public getters/setters provide controlled access.
 *    - This protects data integrity - you can add validation in setters.
 *
 * 3. CONSTRUCTORS
 *    - Default (no-arg) constructor: Required by JPA/Hibernate to
 *      create objects using reflection.
 *    - Parameterized constructor: Convenient way to create objects
 *      with values.
 *
 * JPA/HIBERNATE CONCEPTS:
 * -----------------------
 * @Entity  - Tells JPA "this class maps to a database table"
 * @Table   - Specifies the table name (optional if class name matches)
 * @Id      - Marks the primary key field
 * @GeneratedValue - Auto-generates the ID value
 * @Column  - Customizes column properties (nullable, unique, length)
 * @OneToMany - Defines a one-to-many relationship
 *
 * DATABASE TABLE CREATED:
 * -----------------------
 * CREATE TABLE students (
 *     id          BIGSERIAL PRIMARY KEY,
 *     name        VARCHAR(100) NOT NULL,
 *     email       VARCHAR(100) NOT NULL UNIQUE,
 *     department  VARCHAR(100) NOT NULL,
 *     year        INTEGER NOT NULL
 * );
 *
 * INTERVIEW QUESTIONS:
 * --------------------
 * Q: "Why do we need a no-arg constructor in an entity?"
 * A: "JPA/Hibernate uses reflection to create entity objects.
 *     Reflection calls Class.newInstance() which requires a no-arg
 *     constructor. Without it, Hibernate cannot instantiate the entity."
 *
 * Q: "What is the difference between @Entity and @Table?"
 * A: "@Entity marks a class as a JPA entity (mandatory).
 *     @Table specifies the database table name and is optional.
 *     If @Table is not used, the table name defaults to the class name."
 *
 * Q: "Why use Long for the id instead of int?"
 * A: "Long can hold much larger values than int (up to 9.2 quintillion).
 *     For database primary keys that auto-increment, Long ensures we
 *     never run out of IDs even with millions of records."
 *
 * Q: "What is GenerationType.IDENTITY?"
 * A: "It tells Hibernate to let the database generate the primary key
 *     using its auto-increment feature. For PostgreSQL, this uses
 *     BIGSERIAL which automatically increments the ID for each new row."
 */
@Entity
@Table(name = "students")
public class Student {

    // ==========================================
    // FIELDS (Private - Encapsulation)
    // ==========================================

    /**
     * Primary Key - Unique identifier for each student.
     *
     * @Id = Marks this as the primary key
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     *   = Database auto-generates this value (1, 2, 3, ...)
     *   PostgreSQL uses BIGSERIAL internally for this.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Student's full name.
     * @Column(nullable = false) = This column cannot be NULL in the database.
     * length = 100 = Maximum 100 characters.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Student's email address.
     * unique = true means no two students can have the same email.
     * This creates a UNIQUE constraint in the database.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Student's department (e.g., "Computer Science", "Mechanical").
     */
    @Column(nullable = false, length = 100)
    private String department;

    /**
     * Student's year of study (1, 2, 3, or 4).
     *
     * We use Integer (wrapper class) instead of int (primitive) because:
     * - Integer can be null, int cannot.
     * - JPA needs to distinguish between "not set" (null) and "set to 0".
     */
    @Column(nullable = false)
    private Integer year;

    /**
     * List of service requests created by this student.
     *
     * @OneToMany = One student can create MANY service requests.
     * mappedBy = "student" = The ServiceRequest entity owns this relationship
     *            (it has the foreign key column "student_id").
     *
     * Why mappedBy?
     * - In a bidirectional relationship, ONE side must be the "owner"
     *   (the side with the foreign key = ServiceRequest).
     * - The OTHER side uses mappedBy to say "I'm the inverse side,
     *   the relationship is managed by the 'student' field in ServiceRequest."
     *
     * INTERVIEW Q: "What is the difference between the owning side and inverse side?"
     * A: "The owning side is the entity that contains the foreign key column
     *     (ServiceRequest has student_id). The inverse side uses mappedBy
     *     to reference the owning side. Only changes to the owning side
     *     are persisted to the database."
     */
    @OneToMany(mappedBy = "student")
    private List<ServiceRequest> serviceRequests = new ArrayList<>();

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    /**
     * Default (No-Argument) Constructor.
     * REQUIRED by JPA/Hibernate.
     *
     * Why? Hibernate uses Java Reflection to create objects:
     *   Student s = Student.class.getDeclaredConstructor().newInstance();
     * This only works if a no-arg constructor exists.
     */
    public Student() {
    }

    /**
     * Parameterized Constructor.
     * Convenient way to create a Student object with all required fields.
     *
     * Note: We don't include 'id' because it's auto-generated by the database.
     * Note: We don't include 'serviceRequests' because it starts as empty.
     */
    public Student(String name, String email, String department, Integer year) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================
    // These provide controlled access to private fields (Encapsulation).
    //
    // Why not make fields public?
    // 1. You can add validation in setters (e.g., year must be 1-4)
    // 2. You can make fields read-only by not providing a setter
    // 3. You can change the internal representation without affecting users
    // 4. JPA/Jackson use getters/setters to read/write field values

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

    public List<ServiceRequest> getServiceRequests() {
        return serviceRequests;
    }

    public void setServiceRequests(List<ServiceRequest> serviceRequests) {
        this.serviceRequests = serviceRequests;
    }

    // ==========================================
    // toString() METHOD
    // ==========================================
    /**
     * Returns a string representation of this Student.
     * Useful for logging and debugging.
     *
     * This is METHOD OVERRIDING - we override the toString()
     * method inherited from the Object class.
     *
     * Note: We intentionally exclude serviceRequests from toString()
     * to avoid infinite recursion (Student -> ServiceRequest -> Student -> ...)
     */
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", year=" + year +
                '}';
    }
}
