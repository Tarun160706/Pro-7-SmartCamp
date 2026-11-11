package com.campus.service.service;

import com.campus.service.dto.TechnicianDTO;
import com.campus.service.entity.Technician;
import com.campus.service.exception.DuplicateResourceException;
import com.campus.service.exception.ResourceNotFoundException;
import com.campus.service.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE: TechnicianService
 * ============================================================
 *
 * Contains business logic for Technician operations.
 * Similar structure to StudentService - demonstrating consistency
 * in the layered architecture pattern.
 */
@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    /**
     * Constructor Injection - Spring auto-injects TechnicianRepository.
     */
    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    // ==========================================
    // BUSINESS METHODS
    // ==========================================

    /**
     * CREATE a new technician.
     * Business Rule: Email must be unique.
     */
    public TechnicianDTO createTechnician(TechnicianDTO technicianDTO) {
        if (technicianRepository.existsByEmail(technicianDTO.getEmail())) {
            throw new DuplicateResourceException(
                "Technician with email '" + technicianDTO.getEmail() + "' already exists");
        }

        Technician technician = mapToEntity(technicianDTO);
        Technician savedTechnician = technicianRepository.save(technician);
        return mapToDTO(savedTechnician);
    }

    /**
     * GET all technicians.
     */
    public List<TechnicianDTO> getAllTechnicians() {
        return technicianRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET a technician by ID.
     */
    public TechnicianDTO getTechnicianById(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Technician not found with ID: " + id));
        return mapToDTO(technician);
    }

    // ==========================================
    // DTO <-> ENTITY CONVERSION
    // ==========================================

    private Technician mapToEntity(TechnicianDTO dto) {
        Technician technician = new Technician();
        technician.setName(dto.getName());
        technician.setEmail(dto.getEmail());
        technician.setSpecialization(dto.getSpecialization());
        return technician;
    }

    private TechnicianDTO mapToDTO(Technician technician) {
        return new TechnicianDTO(
                technician.getId(),
                technician.getName(),
                technician.getEmail(),
                technician.getSpecialization()
        );
    }
}