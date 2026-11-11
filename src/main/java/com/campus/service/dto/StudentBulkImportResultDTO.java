package com.campus.service.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentBulkImportResultDTO {

    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private List<String> errors = new ArrayList<>();
    private List<StudentCredentialDTO> credentials = new ArrayList<>();

    public StudentBulkImportResultDTO() {}

    public int getTotalProcessed() {
        return totalProcessed;
    }

    public void setTotalProcessed(int totalProcessed) {
        this.totalProcessed = totalProcessed;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<StudentCredentialDTO> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<StudentCredentialDTO> credentials) {
        this.credentials = credentials;
    }
}