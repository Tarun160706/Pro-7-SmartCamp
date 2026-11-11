package com.campus.service.exception;

/**
 * ============================================================
 * EXCEPTION: ResourceNotFoundException
 * ============================================================
 *
 * Thrown when a requested resource (Student, Technician, ServiceRequest)
 * is not found in the database.
 *
 * Examples:
 * - GET /api/students/999 -> Student with ID 999 does not exist
 * - PUT /api/requests/50/assign/10 -> Technician with ID 10 does not exist
 *
 * JAVA CONCEPTS:
 *
 * 1. INHERITANCE:
 *    This class extends RuntimeException.
 *    RuntimeException is an UNCHECKED exception (does not need try-catch).
 *
 *    Exception hierarchy:
 *    Throwable
 *      ├── Error (JVM errors - out of memory, stack overflow)
 *      └── Exception
 *            ├── Checked Exceptions (must be caught or declared with 'throws')
 *            │     e.g., IOException, SQLException
 *            └── RuntimeException (UNCHECKED - can be thrown without declaration)
 *                  e.g., NullPointerException, IllegalArgumentException
 *                  └── ResourceNotFoundException (our custom exception)
 *
 * 2. WHY RuntimeException (unchecked)?
 *    - We don't want every method in the call chain to declare 'throws'.
 *    - Spring's @RestControllerAdvice catches these automatically.
 *    - It keeps the code clean - no try-catch clutter everywhere.
 *
 * 3. CONSTRUCTOR CHAINING:
 *    super(message) calls the parent class (RuntimeException) constructor,
 *    which stores the error message.
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "What is the difference between checked and unchecked exceptions?"
 * A: "Checked exceptions (like IOException) MUST be caught or declared
 *     with 'throws'. The compiler enforces this. Unchecked exceptions
 *     (like RuntimeException) do NOT need to be caught - the compiler
 *     doesn't enforce it. I use unchecked exceptions for business
 *     logic errors because Spring handles them via @RestControllerAdvice."
 *
 * Q: "Why create a custom exception instead of using a generic one?"
 * A: "Custom exceptions provide meaningful names and messages. When I see
 *     ResourceNotFoundException in the logs, I immediately know the problem.
 *     A generic RuntimeException('not found') doesn't tell me what went wrong.
 *     Custom exceptions also let me handle different errors differently
 *     in the global exception handler."
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with a custom error message.
     *
     * Usage: throw new ResourceNotFoundException("Student not found with ID: 5");
     *
     * super(message) passes the message to RuntimeException's constructor,
     * which stores it. It can be retrieved later with getMessage().
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}