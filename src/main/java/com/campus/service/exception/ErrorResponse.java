package com.campus.service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================
 * DTO: ErrorResponse
 * ============================================================
 *
 * Consistent JSON structure returned to clients whenever an error occurs.
 *
 * Why is a consistent error format important in REST APIs?
 * 1. Client predictability: Frontend/mobile apps can write a single
 *    error-handling interceptor instead of guessing response shapes.
 * 2. Clean debugging: Includes timestamp, HTTP status, clear message,
 *    and the requested endpoint path.
 * 3. Validation details: For form validation errors, contains a field-by-field
 *    breakdown of what went wrong.
 *
 * @JsonInclude(JsonInclude.Include.NON_NULL) ensures validationErrors is omitted
 * from JSON when it is null (keeps clean payloads for non-validation errors).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    // ==========================================
    // GETTERS & SETTERS (Required by Jackson)
    // ==========================================

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}