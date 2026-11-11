package com.campus.service.dto;

public class StudentCredentialDTO {

    private Long studentId;
    private String name;
    private String email;
    private String department;
    private Integer year;
    private String generatedPassword;

    public StudentCredentialDTO() {}

    public StudentCredentialDTO(Long studentId, String name, String email, String department, Integer year, String generatedPassword) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.generatedPassword = generatedPassword;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }
}