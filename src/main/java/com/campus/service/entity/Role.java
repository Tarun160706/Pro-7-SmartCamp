package com.campus.service.entity;

/**
 * User roles for Role-Based Access Control (RBAC).
 *
 * 1. ADMIN: Can view all tickets, assign workers, manage accounts, bulk upload students.
 * 2. STUDENT: Can report issues and track their own reported requests.
 * 3. WORKER: Can view assigned tasks, update progress, and notify admin when completed.
 */
public enum Role {
    ADMIN,
    STUDENT,
    WORKER
}