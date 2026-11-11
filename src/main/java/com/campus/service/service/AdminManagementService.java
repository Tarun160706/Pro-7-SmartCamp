package com.campus.service.service;

import com.campus.service.dto.CreateUserDTO;
import com.campus.service.dto.StudentBulkImportResultDTO;
import com.campus.service.dto.StudentCredentialDTO;
import com.campus.service.entity.Role;
import com.campus.service.entity.Student;
import com.campus.service.entity.Technician;
import com.campus.service.entity.User;
import com.campus.service.exception.DuplicateResourceException;
import com.campus.service.repository.StudentRepository;
import com.campus.service.repository.TechnicianRepository;
import com.campus.service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ============================================================
 * SERVICE: AdminManagementService
 * ============================================================
 *
 * Dedicated admin capabilities:
 * 1. Manual student & worker account creation
 * 2. Bulk student CSV import with auto-generated secure passwords
 * 3. Export of student credentials for distribution
 */
@Service
public class AdminManagementService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TechnicianRepository technicianRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Thread-safe store for recently created credentials available for admin export
    private final List<StudentCredentialDTO> exportedCredentialsCache = new CopyOnWriteArrayList<>();

    public AdminManagementService(UserRepository userRepository,
                                  StudentRepository studentRepository,
                                  TechnicianRepository technicianRepository,
                                  AuthService authService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.technicianRepository = technicianRepository;
        this.passwordEncoder = authService.getPasswordEncoder();
    }

    /**
     * Admin manually creates a single Student account.
     */
    @Transactional
    public StudentCredentialDTO createStudentAccount(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()) || studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Account with email '" + dto.getEmail() + "' already exists");
        }

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? dto.getPassword().trim()
                : generateRandomPassword();

        // 1. Create Student profile
        Student student = new Student(
                dto.getName(),
                dto.getEmail(),
                dto.getDepartment() != null ? dto.getDepartment() : "General",
                dto.getYear() != null ? dto.getYear() : 1
        );
        Student savedStudent = studentRepository.save(student);

        // 2. Create User account
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(rawPassword),
                dto.getName(),
                Role.STUDENT
        );
        user.setStudentId(savedStudent.getId());
        userRepository.save(user);

        StudentCredentialDTO cred = new StudentCredentialDTO(
                savedStudent.getId(),
                savedStudent.getName(),
                savedStudent.getEmail(),
                savedStudent.getDepartment(),
                savedStudent.getYear(),
                rawPassword
        );
        exportedCredentialsCache.add(cred);
        return cred;
    }

    /**
     * Admin manually creates a single Worker (Technician) account.
     */
    @Transactional
    public User createWorkerAccount(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()) || technicianRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Worker account with email '" + dto.getEmail() + "' already exists");
        }

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? dto.getPassword().trim()
                : "Worker@123";

        // 1. Create Technician profile
        Technician tech = new Technician(
                dto.getName(),
                dto.getEmail(),
                dto.getSpecialization() != null ? dto.getSpecialization() : "General Maintenance"
        );
        Technician savedTech = technicianRepository.save(tech);

        // 2. Create User account
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(rawPassword),
                dto.getName(),
                Role.WORKER
        );
        user.setTechnicianId(savedTech.getId());
        return userRepository.save(user);
    }

    /**
     * Bulk import students via CSV text:
     * Format: Name,Email,Department,Year
     */
    @Transactional
    public StudentBulkImportResultDTO bulkImportStudents(String csvContent) {
        StudentBulkImportResultDTO result = new StudentBulkImportResultDTO();
        if (csvContent == null || csvContent.isBlank()) {
            return result;
        }

        String[] lines = csvContent.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // Skip header line if present
            if (line.toLowerCase().startsWith("name,") || line.toLowerCase().startsWith("student")) {
                continue;
            }

            result.setTotalProcessed(result.getTotalProcessed() + 1);

            try {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    result.setFailureCount(result.getFailureCount() + 1);
                    result.getErrors().add("Invalid format on line: " + line + ". Expected: Name,Email,Department,Year");
                    continue;
                }

                String name = parts[0].trim();
                String email = parts[1].trim();
                String department = parts[2].trim();
                int year = Integer.parseInt(parts[3].trim());

                if (userRepository.existsByEmail(email) || studentRepository.existsByEmail(email)) {
                    result.setFailureCount(result.getFailureCount() + 1);
                    result.getErrors().add("Skipped existing email: " + email);
                    continue;
                }

                CreateUserDTO dto = new CreateUserDTO();
                dto.setName(name);
                dto.setEmail(email);
                dto.setDepartment(department);
                dto.setYear(year);
                dto.setRole(Role.STUDENT);

                StudentCredentialDTO cred = createStudentAccount(dto);
                result.getCredentials().add(cred);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception ex) {
                result.setFailureCount(result.getFailureCount() + 1);
                result.getErrors().add("Error processing line [" + line + "]: " + ex.getMessage());
            }
        }

        return result;
    }

    /**
     * Export all student credentials formatted as CSV text.
     */
    public String exportCredentialsCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("Student ID,Name,Email,Department,Year,Initial Password\n");

        for (StudentCredentialDTO c : exportedCredentialsCache) {
            csv.append(c.getStudentId()).append(",")
               .append("\"").append(c.getName()).append("\",")
               .append(c.getEmail()).append(",")
               .append("\"").append(c.getDepartment()).append("\",")
               .append(c.getYear()).append(",")
               .append(c.getGeneratedPassword()).append("\n");
        }
        return csv.toString();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<StudentCredentialDTO> getCachedCredentials() {
        return new ArrayList<>(exportedCredentialsCache);
    }

    /**
     * Generate secure 8-character random password (e.g. Camp#92KZ)
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("Camp#");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}