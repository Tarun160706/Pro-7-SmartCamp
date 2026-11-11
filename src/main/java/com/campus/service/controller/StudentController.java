package com.campus.service.controller;

import com.campus.service.dto.StudentDTO;
import com.campus.service.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: StudentController
 * ============================================================
 *
 * Exposes RESTful HTTP endpoints for Student operations.
 *
 * BASE URL: /api/students
 *
 * KEY CONCEPTS:
 *
 * 1. @RestController
 *    - Combines @Controller and @ResponseBody.
 *    - Tells Spring that every method returns data (JSON) directly,
 *      NOT an HTML view/template.
 *    - Spring uses Jackson under the hood to serialize Java objects into JSON.
 *
 * 2. @RequestMapping("/api/students")
 *    - Base route for all endpoints in this controller.
 *    - Standard REST convention: plural nouns (/api/students, not /api/getStudent).
 *
 * 3. ResponseEntity<T>
 *    - Represents the ENTIRE HTTP response: status code, headers, and body.
 *    - Allows us to return proper RESTful status codes:
 *      * 201 CREATED for resource creation
 *      * 200 OK for successful reads/updates
 *
 * 4. @Valid
 *    - Triggers Jakarta Bean Validation on the incoming StudentDTO.
 *    - Validates @NotBlank, @Email, @Min, @Max before the method executes.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    /**
     * Constructor Injection for StudentService.
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * POST /api/students
     * Register / create a new student.
     *
     * @param studentDTO Validated input body
     * @return 201 CREATED with the saved StudentDTO
     */
    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO created = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/students
     * Retrieve all registered students.
     *
     * @return 200 OK with list of StudentDTOs
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/students/{id}
     * Retrieve a single student by ID.
     *
     * @param id Student ID from path
     * @return 200 OK with StudentDTO if found (or 404 via exception handler)
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }
}