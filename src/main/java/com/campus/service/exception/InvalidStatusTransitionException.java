package com.campus.service.exception;

/**
 * ============================================================
 * EXCEPTION: InvalidStatusTransitionException
 * ============================================================
 *
 * Thrown when someone tries to move a service request to an
 * invalid status. For example:
 * - PENDING -> CLOSED (must go through ASSIGNED, IN_PROGRESS, RESOLVED first)
 * - RESOLVED -> ASSIGNED (can't go backward)
 *
 * Valid transitions:
 *   PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}