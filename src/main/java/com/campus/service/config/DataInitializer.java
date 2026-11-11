package com.campus.service.config;

import com.campus.service.entity.*;
import com.campus.service.repository.ServiceRequestRepository;
import com.campus.service.repository.StudentRepository;
import com.campus.service.repository.TechnicianRepository;
import com.campus.service.repository.UserRepository;
import com.campus.service.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * DATA INITIALIZER: DataInitializer (CommandLineRunner)
 * ============================================================
 *
 * Idempotently seeds the database with Admin, Student, and Worker accounts.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(StudentRepository studentRepository,
                           TechnicianRepository technicianRepository,
                           ServiceRequestRepository serviceRequestRepository,
                           UserRepository userRepository,
                           AuthService authService) {
        this.studentRepository = studentRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = authService.getPasswordEncoder();
    }

    @Override
    public void run(String... args) {
        System.out.println(">>> Checking and initializing default campus accounts & data...");

        // 1. Seed or Get Students
        Student s1 = studentRepository.findByEmail("tarun@campus.edu")
                .orElseGet(() -> studentRepository.save(new Student("Tarun Kumar", "tarun@campus.edu", "Computer Science", 3)));

        Student s2 = studentRepository.findByEmail("priya@campus.edu")
                .orElseGet(() -> studentRepository.save(new Student("Priya Sharma", "priya@campus.edu", "Electronics & Communication", 2)));

        Student s3 = studentRepository.findByEmail("rahul@campus.edu")
                .orElseGet(() -> studentRepository.save(new Student("Rahul Verma", "rahul@campus.edu", "Mechanical Engineering", 4)));

        // 2. Seed or Get Technicians
        Technician t1 = technicianRepository.findByEmail("rajesh.tech@campus.edu")
                .orElseGet(() -> technicianRepository.save(new Technician("Rajesh Sharma", "rajesh.tech@campus.edu", "Electrical & Hardware")));

        Technician t2 = technicianRepository.findByEmail("suresh.tech@campus.edu")
                .orElseGet(() -> technicianRepository.save(new Technician("Suresh Patel", "suresh.tech@campus.edu", "Networking & Wi-Fi")));

        // 3. Seed Users (Admin, Students, Workers)
        if (!userRepository.existsByEmail("admin@campus.edu")) {
            User admin = new User("admin@campus.edu", passwordEncoder.encode("Admin@123"), "Campus Administrator", Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Created default Admin account: admin@campus.edu / Admin@123");
        }

        if (!userRepository.existsByEmail(s1.getEmail())) {
            User u1 = new User(s1.getEmail(), passwordEncoder.encode("Student@123"), s1.getName(), Role.STUDENT);
            u1.setStudentId(s1.getId());
            userRepository.save(u1);
        }

        if (!userRepository.existsByEmail(s2.getEmail())) {
            User u2 = new User(s2.getEmail(), passwordEncoder.encode("Student@123"), s2.getName(), Role.STUDENT);
            u2.setStudentId(s2.getId());
            userRepository.save(u2);
        }

        if (!userRepository.existsByEmail(s3.getEmail())) {
            User u3 = new User(s3.getEmail(), passwordEncoder.encode("Student@123"), s3.getName(), Role.STUDENT);
            u3.setStudentId(s3.getId());
            userRepository.save(u3);
        }

        if (!userRepository.existsByEmail(t1.getEmail())) {
            User uw1 = new User(t1.getEmail(), passwordEncoder.encode("Worker@123"), t1.getName(), Role.WORKER);
            uw1.setTechnicianId(t1.getId());
            userRepository.save(uw1);
        }

        if (!userRepository.existsByEmail(t2.getEmail())) {
            User uw2 = new User(t2.getEmail(), passwordEncoder.encode("Worker@123"), t2.getName(), Role.WORKER);
            uw2.setTechnicianId(t2.getId());
            userRepository.save(uw2);
        }

        // 4. Ensure at least one ticket in WORKER_COMPLETED status for Admin review demo
        if (serviceRequestRepository.count() < 3) {
            ServiceRequest r3 = new ServiceRequest(
                    "Main Auditorium Stage Light blown fuse",
                    "Overhead stage spotlight row 2 shut down during rehearsals.",
                    Category.ELECTRICAL,
                    Priority.HIGH,
                    "Auditorium Main Stage",
                    s1
            );
            r3.setTechnician(t1);
            r3.setStatus(RequestStatus.WORKER_COMPLETED);
            r3.setWorkerNotes("Replaced 16A blown fuse and verified voltage load. Spotlight working normally.");
            serviceRequestRepository.save(r3);
        }

        System.out.println(">>> Role accounts verified and ready!");
    }
}