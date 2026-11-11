package com.campus.service.controller;

import com.campus.service.dto.TechnicianDTO;
import com.campus.service.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: TechnicianController
 * ============================================================
 *
 * Exposes RESTful HTTP endpoints for campus Technicians.
 *
 * BASE URL: /api/technicians
 */
@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    /**
     * POST /api/technicians
     * Register a new technician with specialization.
     *
     * @param technicianDTO Validated input body
     * @return 201 CREATED with created TechnicianDTO
     */
    @PostMapping
    public ResponseEntity<TechnicianDTO> createTechnician(@Valid @RequestBody TechnicianDTO technicianDTO) {
        TechnicianDTO created = technicianService.createTechnician(technicianDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/technicians
     * Retrieve all registered campus technicians.
     *
     * @return 200 OK with list of TechnicianDTOs
     */
    @GetMapping
    public ResponseEntity<List<TechnicianDTO>> getAllTechnicians() {
        List<TechnicianDTO> technicians = technicianService.getAllTechnicians();
        return ResponseEntity.ok(technicians);
    }

    /**
     * GET /api/technicians/{id}
     * Retrieve a specific technician by ID.
     *
     * @param id Technician ID
     * @return 200 OK with TechnicianDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianDTO> getTechnicianById(@PathVariable Long id) {
        TechnicianDTO technician = technicianService.getTechnicianById(id);
        return ResponseEntity.ok(technician);
    }
}