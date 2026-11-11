package com.campus.service.entity;

/**
 * Service Request Lifecycle States:
 *
 * PENDING           -> Submitted by student, awaiting admin triage.
 * ASSIGNED          -> Admin assigned ticket to a worker/technician.
 * IN_PROGRESS       -> Worker has started working on the issue.
 * WORKER_COMPLETED  -> Worker finished the job and notified admin.
 * RESOLVED          -> Admin verified worker's fix and replied to student as completed.
 * CLOSED            -> Ticket archived / finalized.
 * REJECTED          -> Invalid / duplicate ticket.
 */
public enum RequestStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    WORKER_COMPLETED,
    RESOLVED,
    CLOSED,
    REJECTED
}