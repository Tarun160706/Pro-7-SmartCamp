package com.campus.service.exception;

/**
 * ============================================================
 * EXCEPTION: DuplicateResourceException
 * ============================================================
 *
 * Thrown when trying to create a resource that already exists.
 * For example: creating a student with an email that is already registered.
 *
 * Returns HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}