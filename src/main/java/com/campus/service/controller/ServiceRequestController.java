package com.campus.service.controller;

import com.campus.service.dto.AdminResolveDTO;
import com.campus.service.dto.WorkerCompleteDTO;
import com.campus.service.dto.ServiceRequestDTO;
import com.campus.service.dto.ServiceRequestResponseDTO;
import com.campus.service.entity.Category;
import com.campus.service.entity.RequestStatus;
import com.campus.service.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: ServiceRequestController
 * ============================================================
 *
 * Core REST Controller for managing campus service tickets.
 * Handles issue reporting, assignment, status workflow,
 * full-text keyword search, filtering, and pagination.
 *
 * BASE URL: /api/requests
 *
 * HTTP VERBS USED:
 * - POST:   Create a new resource (/api/requests)
 * - GET:    Read resources (/api/requests, /api/requests/{id})
 * - PUT:    Update an existing resource (/api/requests/{id}, /assign, /status)
 * - DELETE: Remove a resource (/api/requests/{id})
 */
@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /**
     * POST /api/requests
     * Create a new campus service request.
     *
     * @param dto Validated request details
     * @return 201 CREATED with enriched ServiceRequestResponseDTO
     */
    @PostMapping
    public ResponseEntity<ServiceRequestResponseDTO> createRequest(
            @Valid @RequestBody ServiceRequestDTO dto) {
        ServiceRequestResponseDTO response = serviceRequestService.createServiceRequest(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/requests
     * Retrieve all requests, optionally filtered by status or category.
     *
     * Example URLs:
     * - GET /api/requests
     * - GET /api/requests?status=PENDING
     * - GET /api/requests?category=ELECTRICAL
     */
    @GetMapping
    public ResponseEntity<List<ServiceRequestResponseDTO>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) Category category) {
        
        if (status != null) {
            return ResponseEntity.ok(serviceRequestService.getRequestsByStatus(status));
        }
        if (category != null) {
            return ResponseEntity.ok(serviceRequestService.getRequestsByCategory(category));
        }
        return ResponseEntity.ok(serviceRequestService.getAllRequests());
    }

    /**
     * GET /api/requests/paginated
     * Retrieve paginated and sorted service requests.
     *
     * Example:
     * GET /api/requests/paginated?page=0&size=5&sortBy=createdAt&direction=desc
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<ServiceRequestResponseDTO>> getPaginatedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(serviceRequestService.getAllRequestsPaginated(pageable));
    }

    /**
     * GET /api/requests/{id}
     * Retrieve request by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponseDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRequestService.getRequestById(id));
    }

    /**
     * PUT /api/requests/{id}
     * Update title, description, category, priority, or location of an existing request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceRequestResponseDTO> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO dto) {
        return ResponseEntity.ok(serviceRequestService.updateRequest(id, dto));
    }

    /**
     * DELETE /api/requests/{id}
     * Delete a service request.
     *
     * Returns 204 NO_CONTENT indicating successful deletion with no body.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        serviceRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/requests/{id}/assign/{technicianId}
     * Assign a technician to a request.
     * Automatically transitions request status to ASSIGNED.
     */
    @PutMapping("/{id}/assign/{technicianId}")
    public ResponseEntity<ServiceRequestResponseDTO> assignTechnician(
            @PathVariable Long id,
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(serviceRequestService.assignTechnician(id, technicianId));
    }

    /**
     * PUT /api/requests/{id}/status
     * Transition the status of a request following the state machine.
     *
     * Example: PUT /api/requests/1/status?status=IN_PROGRESS
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequestResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status) {
        return ResponseEntity.ok(serviceRequestService.changeStatus(id, status));
    }

    /**
     * GET /api/requests/search
     * Search requests by keyword matching title, description, or location.
     *
     * Example: GET /api/requests/search?keyword=projector
     */
    @GetMapping("/search")
    public ResponseEntity<List<ServiceRequestResponseDTO>> searchRequests(
            @RequestParam String keyword) {
        return ResponseEntity.ok(serviceRequestService.searchRequests(keyword));
    }

    /**
     * GET /api/requests/technician/{technicianId}
     * Get all requests currently assigned to a specific technician.
     */
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getRequestsByTechnician(
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(serviceRequestService.getRequestsByTechnician(technicianId));
    }
    /**
     * GET /api/requests/student/{studentId}
     * Get all requests submitted by a specific student (Student Portal view).
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getRequestsByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(serviceRequestService.getRequestsForStudent(studentId));
    }

    /**
     * GET /api/requests/worker/{workerId}
     * Get all requests allocated to a specific worker (Worker View).
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<ServiceRequestResponseDTO>> getRequestsByWorker(
            @PathVariable Long workerId) {
        return ResponseEntity.ok(serviceRequestService.getRequestsForWorker(workerId));
    }

    /**
     * PUT /api/requests/{id}/worker-complete
     * Worker completes the physical repair work, enters notes, and notifies admin.
     * Transitions status: IN_PROGRESS -> WORKER_COMPLETED.
     */
    @PutMapping("/{id}/worker-complete")
    public ResponseEntity<ServiceRequestResponseDTO> workerCompleteWork(
            @PathVariable Long id,
            @Valid @RequestBody WorkerCompleteDTO dto) {
        return ResponseEntity.ok(serviceRequestService.workerCompleteWork(id, dto.getWorkerNotes()));
    }

    /**
     * PUT /api/requests/{id}/admin-resolve
     * Admin reviews worker fix, enters resolution remarks to student, and resolves ticket.
     * Transitions status: WORKER_COMPLETED -> RESOLVED.
     */
    @PutMapping("/{id}/admin-resolve")
    public ResponseEntity<ServiceRequestResponseDTO> adminResolveRequest(
            @PathVariable Long id,
            @Valid @RequestBody AdminResolveDTO dto) {
        return ResponseEntity.ok(serviceRequestService.adminResolveRequest(id, dto.getAdminRemarks()));
    }
}