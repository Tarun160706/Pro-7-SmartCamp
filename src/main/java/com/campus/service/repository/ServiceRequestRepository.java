package com.campus.service.repository;

import com.campus.service.entity.Category;
import com.campus.service.entity.RequestStatus;
import com.campus.service.entity.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORY: ServiceRequestRepository
 * ============================================================
 *
 * This is the most feature-rich repository in the project.
 * It handles all database operations for ServiceRequest entities.
 *
 * FEATURES:
 * 1. Standard CRUD (inherited from JpaRepository)
 * 2. Filter by status
 * 3. Filter by category
 * 4. Search by keyword (title, description, location)
 * 5. Find by student ID
 * 6. Find by technician ID
 * 7. Pagination support
 *
 * QUERY METHODS EXPLAINED:
 *
 * Spring Data JPA supports THREE ways to create queries:
 *
 * WAY 1: Method Name Derivation (Simplest)
 *   findByStatus(status) -> Spring parses the method name
 *                         -> generates SQL automatically
 *   Rules:
 *     findBy     = SELECT ... WHERE
 *     And        = AND
 *     Or         = OR
 *     OrderBy    = ORDER BY
 *     Containing = LIKE '%value%'
 *
 * WAY 2: @Query with JPQL (Java Persistence Query Language)
 *   @Query("SELECT s FROM ServiceRequest s WHERE ...")
 *   - JPQL uses entity/field names (ServiceRequest, title)
 *   - NOT table/column names (service_requests, title)
 *   - More flexible than method name derivation
 *
 * WAY 3: @Query with Native SQL
 *   @Query(value = "SELECT * FROM service_requests WHERE ...", nativeQuery = true)
 *   - Uses actual SQL with table/column names
 *   - Database-specific (less portable)
 *   - Use only when JPQL can't express the query
 *
 * We'll use WAY 1 and WAY 2 in this project.
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "How does Spring Data JPA generate queries from method names?"
 * A: "Spring Data JPA parses the method name at startup. It splits
 *     the name into parts: the subject (findBy, countBy, existsBy),
 *     the criteria (field names like Status, Category), and operators
 *     (And, Or, Between, Containing). It then builds a query using
 *     the JPA Criteria API internally."
 *
 * Q: "What is JPQL?"
 * A: "JPQL is Java Persistence Query Language. It's similar to SQL
 *     but uses entity class names and field names instead of table
 *     and column names. For example, 'SELECT s FROM ServiceRequest s'
 *     instead of 'SELECT * FROM service_requests'. JPQL is database-
 *     independent - the same query works with PostgreSQL, MySQL, etc."
 *
 * Q: "What is the difference between JPQL and native SQL?"
 * A: "JPQL uses entity names and is database-independent. Native SQL
 *     uses actual table/column names and is database-specific. JPQL
 *     is preferred for portability. Native SQL is used when you need
 *     database-specific features that JPQL doesn't support."
 *
 * Q: "What is Pageable?"
 * A: "Pageable is a Spring Data interface that holds pagination
 *     information: page number, page size, and sorting. When we pass
 *     Pageable to a repository method, Spring automatically adds
 *     LIMIT and OFFSET to the SQL query and returns a Page object
 *     with the results plus pagination metadata."
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // ========================================
    // FILTER BY STATUS
    // ========================================

    /**
     * Find all service requests with a specific status.
     *
     * Method Name Derivation:
     *   findByStatus -> SELECT * FROM service_requests WHERE status = ?
     *
     * Example usage: Find all PENDING requests.
     *
     * @param status The RequestStatus enum value to filter by
     * @return List of matching service requests
     */
    List<ServiceRequest> findByStatus(RequestStatus status);

    // ========================================
    // FILTER BY CATEGORY
    // ========================================

    /**
     * Find all service requests of a specific category.
     *
     * Method Name Derivation:
     *   findByCategory -> SELECT * FROM service_requests WHERE category = ?
     *
     * Example usage: Find all ELECTRICAL issues.
     */
    List<ServiceRequest> findByCategory(Category category);

    // ========================================
    // FIND BY STUDENT
    // ========================================

    /**
     * Find all service requests created by a specific student.
     *
     * Method Name Derivation:
     *   findByStudentId -> SELECT * FROM service_requests WHERE student_id = ?
     *
     * Note: "StudentId" traverses the relationship:
     *   ServiceRequest -> student (field) -> id (field of Student)
     *   Spring understands this automatically!
     */
    List<ServiceRequest> findByStudentId(Long studentId);

    // ========================================
    // FIND BY TECHNICIAN
    // ========================================

    /**
     * Find all service requests assigned to a specific technician.
     *
     * Method Name Derivation:
     *   findByTechnicianId -> SELECT * FROM service_requests WHERE technician_id = ?
     */
    List<ServiceRequest> findByTechnicianId(Long technicianId);

    // ========================================
    // SEARCH BY KEYWORD
    // ========================================

    /**
     * Search service requests by keyword across title, description, and location.
     *
     * This uses @Query with JPQL because the method name would be too long:
     *   findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase
     *   (ugly and unreadable!)
     *
     * JPQL Explained:
     *   SELECT sr FROM ServiceRequest sr
     *   - sr is an alias for ServiceRequest
     *   - We use entity name (ServiceRequest), not table name (service_requests)
     *
     *   WHERE LOWER(sr.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
     *   - LOWER() makes search case-insensitive
     *   - CONCAT('%', :keyword, '%') creates a wildcard pattern
     *   - :keyword is a named parameter (bound from method parameter)
     *
     * @Param("keyword") binds the method parameter to the JPQL :keyword
     *
     * Generated SQL:
     *   SELECT * FROM service_requests
     *   WHERE LOWER(title) LIKE LOWER('%keyword%')
     *      OR LOWER(description) LIKE LOWER('%keyword%')
     *      OR LOWER(location) LIKE LOWER('%keyword%')
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE " +
           "LOWER(sr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sr.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sr.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ServiceRequest> searchByKeyword(@Param("keyword") String keyword);

    // ========================================
    // PAGINATION SUPPORT
    // ========================================

    /**
     * Find all service requests with pagination.
     *
     * This method is actually inherited from JpaRepository,
     * but we declare it here explicitly for documentation.
     *
     * Pageable contains: page number, page size, sort direction.
     * Page<T> contains: content (list), totalElements, totalPages, etc.
     *
     * Example usage:
     *   Pageable pageable = PageRequest.of(0, 10);  // Page 0, 10 items per page
     *   Page<ServiceRequest> page = repository.findAll(pageable);
     *
     * Generated SQL:
     *   SELECT * FROM service_requests LIMIT 10 OFFSET 0
     *   SELECT COUNT(*) FROM service_requests (for total count)
     */
    // Page<ServiceRequest> findAll(Pageable pageable);
    // ^ This is already inherited from JpaRepository, no need to declare.
    //   Kept here as a comment for learning purposes.
}