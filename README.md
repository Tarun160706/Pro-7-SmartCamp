# 🎓 Smart Campus Service Management System

A backend application built with **Spring Boot 3**, **Spring Data JPA**, and **PostgreSQL** to digitize facility maintenance across university campuses. Students can submit requests for facility issues, and administrators can assign tickets to technicians through a structured lifecycle state machine.

---

## 🌟 Key Features

- **Decoupled 3-Tier Architecture:** Separation between Controller, Service, and Repository layers.
- **DTO Flattening & Decoupling:** Separation of API contracts from database entities.
- **Enforced State Machine Lifecycle:** Strict transition matrix (`PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED`).
- **Full-Text Keyword Search & Pagination:** Search issues across title, description, and location.
- **Robust Bean Validation & Centralized Exception Handling:** RFC-compliant error payloads managed via `@RestControllerAdvice`.
- **Automated Sample Seeder:** Pre-populates students, technicians, and requests on startup using `CommandLineRunner`.

---

## 🛠️ Technology Stack & Architecture

- **Language & Framework:** Java 17 (LTS), Spring Boot 3.2.5 (Web, Data JPA, Validation)
- **Database & ORM:** PostgreSQL 18, Hibernate 6.x, HikariCP connection pool
- **Build Tool:** Maven 3.x (with Maven Wrapper `./mvnw`)

The application follows a standard flow from the `@RestController` layer down through the `@Service` business logic and Spring Data JPA repositories to the PostgreSQL database.

---

## 🚀 REST API & Quick Start

- **Endpoints:** Available for Students (`/api/students`), Technicians (`/api/technicians`), and Service Requests (`/api/requests`) supporting CRUD operations, status transitions, technician assignments, and keyword search.
- **Running Locally:** Set up a PostgreSQL 14+ database (`smart_campus_db`), update your `application.properties`, and run `./mvnw spring-boot:run` to launch the application on `http://localhost:8080`.

## 👨‍💻 Author

- **Tarun R** — Computer Science & Engineering student preparing for Java backend engineering roles.
