package com.campus.service.entity;

/**
 * ============================================================
 * ENUM: Priority
 * ============================================================
 *
 * Represents the urgency level of a service request.
 *
 * Priority levels (from least to most urgent):
 *   LOW → MEDIUM → HIGH → CRITICAL
 *
 * Examples:
 *   LOW      - Furniture has a small scratch
 *   MEDIUM   - Classroom projector is dim
 *   HIGH     - Lab computer not booting before an exam
 *   CRITICAL - Electrical hazard / safety issue
 */
public enum Priority {

    LOW,        // Can be handled when convenient
    MEDIUM,     // Should be handled within a few days
    HIGH,       // Needs attention soon
    CRITICAL    // Requires immediate attention (safety issues)
}
