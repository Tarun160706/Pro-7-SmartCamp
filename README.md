# 🎓 Smart Campus Service Management System

A realistic, production-ready backend application built with **Spring Boot 3**, **Spring Data JPA**, and **PostgreSQL** to streamline and digitize facility maintenance across university campuses.

Students can submit service requests for facility issues (such as broken classroom projectors, lab Wi-Fi outages, electrical failures, and furniture damage), and administrators can assign tickets to specialized technicians and track resolutions through an enforced lifecycle state machine.

---

## 🌟 Key Features

- **Decoupled 3-Tier Architecture:** Clean separation of concerns between Controller, Service, and Repository layers.
- **DTO Flattening & Decoupling:** Complete separation of API contracts from database entities, preventing Jackson recursive serialization loops.
- **Enforced State Machine Lifecycle:** Strict transition matrix (PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED), preventing invalid state jumps.
- **Full-Text Keyword Search & Pagination:** Search issues across title, description, and location; paginated query endpoints via Spring Data JPA.
- **Robust Bean Validation:** Jakarta validation annotations (@NotBlank, @Email, @NotNull, @Min, @Max) guaranteeing input sanitization.
- **Centralized Exception Handling:** Uniform, RFC-compliant error payloads (400, 404, 409, 500) managed application-wide via @RestControllerAdvice.
- **Automated Sample Seeder:** Pre-populates realistic students, technicians, and requests on startup using CommandLineRunner.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 (LTS) |
| **Framework** | Spring Boot 3.2.5 (Web, Data JPA, Validation) |
| **ORM** | Hibernate 6.x |
| **Database** | PostgreSQL 18 |
| **Connection Pool** | HikariCP |
| **Build Tool** | Maven 3.x (with Maven Wrapper ./mvnw) |

---

## 🏛️ System Architecture

`
Client (Browser / Postman / cURL)
               │
               ▼  HTTP JSON
┌───────────────────────────────────────────┐
│     Controller Layer (@RestController)    │  <-- /api/students, /api/technicians, /api/requests
└──────────────────────┬────────────────────┘
                       │  DTOs (Validated via @Valid)
                       ▼
┌───────────────────────────────────────────┐
│       Service Layer (@Service)            │  <-- Business rules, State machine, DTO-Entity mapping
└──────────────────────┬────────────────────┘
                       │  JPA Entities
                       ▼
┌───────────────────────────────────────────┐
│     Repository Layer (Spring Data JPA)    │  <-- JpaRepository, JPQL, Derived queries
└──────────────────────┬────────────────────┘
                       │  HikariCP / JDBC
                       ▼
┌───────────────────────────────────────────┐
│          PostgreSQL Database              │  <-- smart_campus_db (3 tables + FKs)
└───────────────────────────────────────────┘
`

---

## 🗄️ Database Schema & Entities

`
+----------------+          +------------------------+          +-------------------+
|    students    |          |    service_requests    |          |    technicians    |
+----------------+          +------------------------+          +-------------------+
| id (PK)        |<-----\   | id (PK)                |   /----->| id (PK)           |
| name           |       \--| student_id (FK)        |  /       | name              |
| email (UNIQUE) |          | technician_id (FK) ----|-/        | email (UNIQUE)    |
| department     |          | title                  |          | specialization    |
| year           |          | description            |          +-------------------+
+----------------+          | category               |
                            | priority               |
                            | status                 |
                            | location               |
                            | created_at             |
                            | updated_at             |
                            +------------------------+
`

---

## 🚀 REST API Endpoints

### 1. Students (/api/students)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| POST | /api/students | Register a new student | 201 CREATED |
| GET | /api/students | List all registered students | 200 OK |
| GET | /api/students/{id} | Retrieve student by ID | 200 OK |

### 2. Technicians (/api/technicians)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| POST | /api/technicians | Register a new technician | 201 CREATED |
| GET | /api/technicians | List all campus technicians | 200 OK |
| GET | /api/technicians/{id} | Retrieve technician by ID | 200 OK |

### 3. Service Requests (/api/requests)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| POST | /api/requests | Submit a new service request | 201 CREATED |
| GET | /api/requests | List all requests (supports ?status= & ?category=) | 200 OK |
| GET | /api/requests/paginated | Paginated & sorted view (?page=0&size=10&sortBy=createdAt) | 200 OK |
| GET | /api/requests/{id} | Retrieve request details | 200 OK |
| PUT | /api/requests/{id} | Update request details | 200 OK |
| DELETE | /api/requests/{id} | Delete a request | 204 NO CONTENT |
| PUT | /api/requests/{id}/assign/{techId} | Assign technician (auto-updates status to ASSIGNED) | 200 OK |
| PUT | /api/requests/{id}/status?status=... | Transition status according to state machine rules | 200 OK |
| GET | /api/requests/search?keyword=... | Search title, description, or location | 200 OK |
| GET | /api/requests/technician/{techId} | View tickets assigned to a specific technician | 200 OK |

---

## 💻 Quick Start & Running Locally

### 1. Prerequisites
- **Java 17+ (JDK)** installed and configured in JAVA_HOME.
- **PostgreSQL 14+** running on localhost:5432.

### 2. Configure Database
Create the database in PostgreSQL:
`sql
CREATE DATABASE smart_campus_db;
`

Update your src/main/resources/application.properties credentials if different:
`properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_campus_db
spring.datasource.username=postgres
spring.datasource.password=Tarun@123
`

### 3. Build & Run
Run the application with the included Maven wrapper:
`ash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
`

The application will start on http://localhost:8080.
Hibernate will automatically create all tables and foreign keys, and DataInitializer will seed sample campus records on startup!

---

## 🧪 Testing with cURL

### Create a Service Request:
`ash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Classroom 302 Projector flickering",
    "description": "HDMI signal disconnects during lectures",
    "category": "ELECTRICAL",
    "priority": "HIGH",
    "location": "Academic Block A - Room 302",
    "studentId": 1
  }'
`

### Assign a Technician:
`ash
curl -X PUT http://localhost:8080/api/requests/1/assign/1
`

### Update Status:
`ash
curl -X PUT "http://localhost:8080/api/requests/1/status?status=IN_PROGRESS"
`

---

## 👨‍💻 Author
- **Tarun Kumar** — 3rd-year Computer Science & Engineering student preparing for Java backend engineering roles.#   P r o - 7 - S m a r t C a m p  
 