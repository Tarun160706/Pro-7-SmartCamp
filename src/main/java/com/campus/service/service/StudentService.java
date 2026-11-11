package com.campus.service.service;

import com.campus.service.dto.StudentDTO;
import com.campus.service.entity.Student;
import com.campus.service.exception.DuplicateResourceException;
import com.campus.service.exception.ResourceNotFoundException;
import com.campus.service.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE: StudentService
 * ============================================================
 *
 * Contains all business logic related to Student operations.
 *
 * KEY CONCEPTS:
 *
 * 1. @Service ANNOTATION
 *    - Marks this class as a Spring-managed service bean.
 *    - Spring creates ONE instance (singleton) and manages its lifecycle.
 *    - It's a specialization of @Component with the same behavior,
 *      but using @Service makes the intent clear: "this holds business logic."
 *
 * 2. DEPENDENCY INJECTION (DI)
 *    - This service DEPENDS on StudentRepository to access the database.
 *    - Instead of creating the repository ourselves:
 *        StudentRepository repo = new StudentRepositoryImpl();  // BAD - tight coupling
 *    - We let Spring INJECT it through the constructor:
 *        public StudentService(StudentRepository repo) { ... }  // GOOD - loose coupling
 *    - Spring automatically finds the StudentRepository bean and passes it.
 *
 *    WHY CONSTRUCTOR INJECTION (not @Autowired on field)?
 *    - Fields are immutable (final) - prevents accidental reassignment
 *    - Dependencies are explicit - you can see what this class needs
 *    - Easier to test - you can pass mock objects in unit tests
 *    - Recommended by the Spring team
 *
 * 3. DTO <-> ENTITY CONVERSION
 *    - The service converts between DTOs (API layer) and Entities (DB layer).
 *    - Controller receives DTO -> Service converts to Entity -> Repository saves Entity
 *    - Repository returns Entity -> Service converts to DTO -> Controller returns DTO
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "What is @Service?"
 * A: "It's a Spring stereotype annotation that marks a class as a service
 *     component. Spring auto-detects it during component scanning and creates
 *     a singleton bean. It indicates the class contains business logic."
 *
 * Q: "What is Dependency Injection?"
 * A: "DI is a design pattern where an object receives its dependencies
 *     from an external source (Spring container) instead of creating
 *     them itself. This promotes loose coupling - my service depends
 *     on the StudentRepository interface, not a specific implementation."
 *
 * Q: "Why constructor injection over @Autowired on fields?"
 * A: "Constructor injection makes dependencies explicit, allows fields
 *     to be final (immutable), makes the class easier to test with mocks,
 *     and is the approach recommended by the Spring team."
 *
 * Q: "What is the Singleton pattern here?"
 * A: "By default, Spring creates only ONE instance of each bean (singleton scope).
 *     Every class that depends on StudentService gets the SAME instance.
 *     This is efficient and safe because our services are stateless."
 */
@Service
public class StudentService {

    // ==========================================
    // DEPENDENCY (injected by Spring)
    // ==========================================

    /**
     * 'final' means this field can only be set once (in the constructor).
     * This ensures the repository reference never changes after construction.
     */
    private final StudentRepository studentRepository;

    /**
     * CONSTRUCTOR INJECTION
     *
     * Spring sees this constructor, finds a bean of type StudentRepository,
     * and automatically passes it here. This is Dependency Injection.
     *
     * Note: When there's only ONE constructor, @Autowired is optional.
     * Spring auto-detects it. We keep the code clean by not adding it.
     */
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ==========================================
    // BUSINESS METHODS
    // ==========================================

    /**
     * CREATE a new student.
     *
     * Business Rules:
     * 1. Email must not already exist (prevent duplicates).
     *
     * Flow: DTO -> check duplicate -> convert to Entity -> save -> convert back to DTO
     */
    public StudentDTO createStudent(StudentDTO studentDTO) {
        // Business Rule: Check if email already exists
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new DuplicateResourceException(
                "Student with email '" + studentDTO.getEmail() + "' already exists");
        }

        // Convert DTO to Entity
        Student student = mapToEntity(studentDTO);

        // Save to database (JpaRepository.save() does INSERT for new entities)
        Student savedStudent = studentRepository.save(student);

        // Convert saved Entity back to DTO and return
        return mapToDTO(savedStudent);
    }

    /**
     * GET all students.
     *
     * Uses Java Stream API to convert List<Student> to List<StudentDTO>.
     *
     * Stream API explained:
     *   .stream()          = Create a stream from the list
     *   .map(this::mapToDTO) = Transform each Student to StudentDTO
     *   .collect(Collectors.toList()) = Collect results into a new List
     *
     * 'this::mapToDTO' is a METHOD REFERENCE (Java 8+).
     * It's shorthand for: student -> this.mapToDTO(student)
     */
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET a student by ID.
     *
     * Uses Optional.orElseThrow():
     * - findById() returns Optional<Student>
     * - If present: unwraps and returns the Student
     * - If empty: throws ResourceNotFoundException
     *
     * This is much cleaner than:
     *   Student s = repo.findById(id);
     *   if (s == null) throw new NotFoundException(...);
     *   return s;
     */
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Student not found with ID: " + id));
        return mapToDTO(student);
    }

    // ==========================================
    // DTO <-> ENTITY CONVERSION METHODS
    // ==========================================

    /**
     * Convert StudentDTO -> Student Entity
     *
     * Used when RECEIVING data from the client.
     * The DTO has the data the client sent;
     * we need to convert it to an Entity for the database.
     */
    private Student mapToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setDepartment(dto.getDepartment());
        student.setYear(dto.getYear());
        return student;
    }

    /**
     * Convert Student Entity -> StudentDTO
     *
     * Used when SENDING data to the client.
     * The Entity has the database data;
     * we need to convert it to a DTO for the API response.
     */
    private StudentDTO mapToDTO(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getDepartment(),
                student.getYear()
        );
    }
}