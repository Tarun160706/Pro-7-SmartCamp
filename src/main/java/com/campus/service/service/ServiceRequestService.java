package com.campus.service.service;

import com.campus.service.dto.ServiceRequestDTO;
import com.campus.service.dto.ServiceRequestResponseDTO;
import com.campus.service.entity.Category;
import com.campus.service.entity.RequestStatus;
import com.campus.service.entity.ServiceRequest;
import com.campus.service.entity.Student;
import com.campus.service.entity.Technician;
import com.campus.service.exception.InvalidStatusTransitionException;
import com.campus.service.exception.ResourceNotFoundException;
import com.campus.service.repository.ServiceRequestRepository;
import com.campus.service.repository.StudentRepository;
import com.campus.service.repository.TechnicianRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE: ServiceRequestService
 * ============================================================
 *
 * This is the MOST IMPORTANT service in the application.
 * It contains ALL business logic for service request operations:
 *
 * 1. CRUD operations (Create, Read, Update, Delete)
 * 2. Status transition validation (PENDING -> ASSIGNED -> ... -> CLOSED)
 * 3. Technician assignment
 * 4. Search by keyword
 * 5. Filter by status/category
 * 6. Pagination
 *
 * BUSINESS RULES ENFORCED:
 * - A request can only be assigned to an existing technician
 * - Status transitions must follow the valid lifecycle
 * - A request cannot jump from PENDING directly to CLOSED
 * - When assigning a technician, status auto-changes to ASSIGNED
 * - Deleting a non-existing request returns 404
 *
 * JAVA CONCEPTS:
 * - Map (HashMap) - for defining valid status transitions
 * - Set (HashSet) - for storing allowed next statuses
 * - Stream API - for list transformations
 * - Optional - for handling nullable results
 *
 * INTERVIEW Q: "Why does ServiceRequestService have 3 repository dependencies?"
 * A: "Because service requests are related to both students and technicians.
 *     When creating a request, I need StudentRepository to verify the student
 *     exists. When assigning a technician, I need TechnicianRepository to
 *     verify the technician exists. And I need ServiceRequestRepository
 *     for the actual CRUD operations on requests."
 */
@Service
public class ServiceRequestService {

    // ==========================================
    // DEPENDENCIES (all injected by Spring)
    // ==========================================

    private final ServiceRequestRepository serviceRequestRepository;
    private final StudentRepository studentRepository;
    private final TechnicianRepository technicianRepository;

    /**
     * VALID STATUS TRANSITIONS
     *
     * This Map defines which statuses can transition to which.
     * Key = current status, Value = set of allowed next statuses.
     *
     * Valid flow:
     *   PENDING     -> ASSIGNED
     *   ASSIGNED    -> IN_PROGRESS
     *   IN_PROGRESS -> RESOLVED
     *   RESOLVED    -> CLOSED
     *
     * Java concept: Map<K,V> is a collection that stores key-value pairs.
     * Map.of() creates an immutable (unmodifiable) map.
     * Set.of() creates an immutable set.
     *
     * INTERVIEW Q: "How did you implement status transition validation?"
     * A: "I used a Map where each key is a current status and the value
     *     is a Set of allowed next statuses. Before changing status,
     *     I check if the new status is in the allowed set. If not,
     *     I throw an InvalidStatusTransitionException. This approach
     *     is clean, maintainable, and easy to extend."
     */
    private static final Map<RequestStatus, Set<RequestStatus>> VALID_TRANSITIONS = Map.of(
        RequestStatus.PENDING,          Set.of(RequestStatus.ASSIGNED, RequestStatus.REJECTED),
        RequestStatus.ASSIGNED,         Set.of(RequestStatus.IN_PROGRESS, RequestStatus.REJECTED),
        RequestStatus.IN_PROGRESS,      Set.of(RequestStatus.WORKER_COMPLETED),
        RequestStatus.WORKER_COMPLETED, Set.of(RequestStatus.RESOLVED, RequestStatus.IN_PROGRESS),
        RequestStatus.RESOLVED,         Set.of(RequestStatus.CLOSED)
    );

    /**
     * Constructor injection with THREE dependencies.
     * Spring finds and injects all three repositories automatically.
     */
    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                  StudentRepository studentRepository,
                                  TechnicianRepository technicianRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.studentRepository = studentRepository;
        this.technicianRepository = technicianRepository;
    }

    // ==========================================
    // CREATE
    // ==========================================

    /**
     * Create a new service request.
     *
     * Business Rules:
     * 1. The student (studentId) must exist in the database.
     * 2. Status is automatically set to PENDING.
     * 3. Timestamps are auto-set by @PrePersist.
     */
    public ServiceRequestResponseDTO createServiceRequest(ServiceRequestDTO dto) {
        // Verify student exists
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Student not found with ID: " + dto.getStudentId()));

        // Convert DTO to Entity
        ServiceRequest request = new ServiceRequest();
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setCategory(dto.getCategory());
        request.setPriority(dto.getPriority());
        request.setLocation(dto.getLocation());
        request.setStudent(student);
        // status, createdAt, updatedAt are auto-set by @PrePersist

        // Save and return
        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        return mapToResponseDTO(savedRequest);
    }

    // ==========================================
    // READ
    // ==========================================

    /**
     * Get all service requests (without pagination).
     */
    public List<ServiceRequestResponseDTO> getAllRequests() {
        return serviceRequestRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all service requests WITH pagination.
     *
     * Pageable contains: page number, page size, sort info.
     * Page<T> contains: content list + pagination metadata.
     *
     * The .map() on Page is different from Stream.map():
     * Page has its own .map() method that transforms the content
     * while preserving pagination metadata (totalElements, totalPages).
     */
    public Page<ServiceRequestResponseDTO> getAllRequestsPaginated(Pageable pageable) {
        return serviceRequestRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    /**
     * Get a single service request by ID.
     */
    public ServiceRequestResponseDTO getRequestById(Long id) {
        ServiceRequest request = findRequestOrThrow(id);
        return mapToResponseDTO(request);
    }

    // ==========================================
    // UPDATE
    // ==========================================

    /**
     * Update a service request (title, description, category, priority, location).
     *
     * Note: This does NOT change the status or technician assignment.
     * Those have dedicated endpoints with their own business rules.
     */
    public ServiceRequestResponseDTO updateRequest(Long id, ServiceRequestDTO dto) {
        ServiceRequest request = findRequestOrThrow(id);

        // Update only the fields that can be modified
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setCategory(dto.getCategory());
        request.setPriority(dto.getPriority());
        request.setLocation(dto.getLocation());
        // updatedAt is auto-set by @PreUpdate

        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return mapToResponseDTO(updatedRequest);
    }

    // ==========================================
    // DELETE
    // ==========================================

    /**
     * Delete a service request.
     * Business Rule: The request must exist, otherwise return 404.
     */
    public void deleteRequest(Long id) {
        if (!serviceRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Service request not found with ID: " + id);
        }
        serviceRequestRepository.deleteById(id);
    }

    // ==========================================
    // ASSIGN TECHNICIAN
    // ==========================================

    /**
     * Assign a technician to a service request.
     *
     * Business Rules:
     * 1. The service request must exist.
     * 2. The technician must exist.
     * 3. When assigned, status automatically changes to ASSIGNED.
     */
    public ServiceRequestResponseDTO assignTechnician(Long requestId, Long technicianId) {
        ServiceRequest request = findRequestOrThrow(requestId);

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Technician not found with ID: " + technicianId));

        request.setTechnician(technician);
        request.setStatus(RequestStatus.ASSIGNED);

        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return mapToResponseDTO(updatedRequest);
    }

    // ==========================================
    // CHANGE STATUS
    // ==========================================

    /**
     * Change the status of a service request.
     *
     * This is the most important business logic method.
     *
     * Business Rules:
     * 1. The request must exist.
     * 2. The transition must be valid (checked against VALID_TRANSITIONS map).
     * 3. CLOSED requests cannot be changed further.
     *
     * Valid transitions:
     *   PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
     */
    public ServiceRequestResponseDTO changeStatus(Long id, RequestStatus newStatus) {
        ServiceRequest request = findRequestOrThrow(id);
        RequestStatus currentStatus = request.getStatus();

        // Check if current status is a terminal state (CLOSED)
        if (!VALID_TRANSITIONS.containsKey(currentStatus)) {
            throw new InvalidStatusTransitionException(
                "Cannot change status from " + currentStatus +
                ". It is a terminal state.");
        }

        // Check if the transition is valid
        Set<RequestStatus> allowedNextStatuses = VALID_TRANSITIONS.get(currentStatus);
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                "Invalid status transition from " + currentStatus +
                " to " + newStatus + ". Allowed transitions: " + allowedNextStatuses);
        }

        request.setStatus(newStatus);
        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return mapToResponseDTO(updatedRequest);
    }

    // ==========================================
    // SEARCH AND FILTER
    // ==========================================

    /**
     * Search service requests by keyword.
     * Searches across title, description, and location fields.
     */
    public List<ServiceRequestResponseDTO> searchRequests(String keyword) {
        return serviceRequestRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filter service requests by status.
     */
    public List<ServiceRequestResponseDTO> getRequestsByStatus(RequestStatus status) {
        return serviceRequestRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filter service requests by category.
     */
    public List<ServiceRequestResponseDTO> getRequestsByCategory(Category category) {
        return serviceRequestRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all requests assigned to a specific technician.
     */
    public List<ServiceRequestResponseDTO> getRequestsByTechnician(Long technicianId) {
        // Verify technician exists first
        if (!technicianRepository.existsById(technicianId)) {
            throw new ResourceNotFoundException(
                "Technician not found with ID: " + technicianId);
        }
        return serviceRequestRepository.findByTechnicianId(technicianId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Find a service request by ID or throw ResourceNotFoundException.
     *
     * This is a private helper method used by multiple public methods.
     * It avoids duplicating the same findById + orElseThrow logic.
     * This is the DRY principle: Don't Repeat Yourself.
     */
    private ServiceRequest findRequestOrThrow(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Service request not found with ID: " + id));
    }

    /**
     * Convert ServiceRequest Entity -> ServiceRequestResponseDTO
     *
     * This is the FLATTENING logic:
     * - Extracts student name/email from the Student entity
     * - Extracts technician name from the Technician entity (if assigned)
     * - Avoids sending full nested objects (prevents infinite recursion)
     */
    private ServiceRequestResponseDTO mapToResponseDTO(ServiceRequest request) {
        ServiceRequestResponseDTO dto = new ServiceRequestResponseDTO();

        // Request details
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setCategory(request.getCategory());
        dto.setPriority(request.getPriority());
        dto.setStatus(request.getStatus());
        dto.setLocation(request.getLocation());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        // Flatten student info
        if (request.getStudent() != null) {
            dto.setStudentId(request.getStudent().getId());
            dto.setStudentName(request.getStudent().getName());
            dto.setStudentEmail(request.getStudent().getEmail());
        }

        // Flatten technician info (may be null if not assigned yet)
        if (request.getTechnician() != null) {
            dto.setTechnicianId(request.getTechnician().getId());
            dto.setTechnicianName(request.getTechnician().getName());
        }

        dto.setWorkerNotes(request.getWorkerNotes());
        dto.setAdminRemarks(request.getAdminRemarks());

        return dto;
    }
    /**
     * WORKER ACTION: Notify admin that work is finished.
     */
    public ServiceRequestResponseDTO workerCompleteWork(Long id, String workerNotes) {
        ServiceRequest request = findRequestOrThrow(id);
        request.setWorkerNotes(workerNotes != null ? workerNotes.trim() : "Work completed by technician.");
        request.setStatus(RequestStatus.WORKER_COMPLETED);
        ServiceRequest saved = serviceRequestRepository.save(request);
        return mapToResponseDTO(saved);
    }

    /**
     * ADMIN ACTION: Confirm resolution, add reply remarks to student, and resolve.
     */
    public ServiceRequestResponseDTO adminResolveRequest(Long id, String adminRemarks) {
        ServiceRequest request = findRequestOrThrow(id);
        request.setAdminRemarks(adminRemarks != null ? adminRemarks.trim() : "Issue verified and resolved by campus administration.");
        request.setStatus(RequestStatus.RESOLVED);
        ServiceRequest saved = serviceRequestRepository.save(request);
        return mapToResponseDTO(saved);
    }

    /**
     * STUDENT VIEW: Get tickets reported by a specific student.
     */
    public List<ServiceRequestResponseDTO> getRequestsForStudent(Long studentId) {
        return serviceRequestRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * WORKER VIEW: Get tickets assigned to a specific worker.
     */
    public List<ServiceRequestResponseDTO> getRequestsForWorker(Long technicianId) {
        return serviceRequestRepository.findByTechnicianId(technicianId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}