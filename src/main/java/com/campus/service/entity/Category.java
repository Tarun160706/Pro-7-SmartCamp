package com.campus.service.entity;

/**
 * ============================================================
 * ENUM: Category
 * ============================================================
 *
 * Represents the type/category of issue being reported.
 *
 * This helps administrators and technicians quickly understand
 * what kind of problem needs to be fixed, and route the request
 * to a technician with the right specialization.
 *
 * Examples:
 *   ELECTRICAL  - Power outlet not working in Room 301
 *   INTERNET    - Wi-Fi down in Library
 *   COMPUTER    - Lab PC #5 won't start
 *   PROJECTOR   - Projector flickering in Lecture Hall A
 *   FURNITURE   - Broken chair in Room 102
 *   CLEANING    - Spill in corridor near Lab 2
 *   OTHER       - Any issue that doesn't fit above categories
 */
public enum Category {

    ELECTRICAL,
    INTERNET,
    COMPUTER,
    PROJECTOR,
    FURNITURE,
    CLEANING,
    OTHER
}
