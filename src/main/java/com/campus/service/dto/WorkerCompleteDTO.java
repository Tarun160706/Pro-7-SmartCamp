package com.campus.service.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkerCompleteDTO {

    @NotBlank(message = "Worker completion notes cannot be blank")
    private String workerNotes;

    public WorkerCompleteDTO() {}

    public WorkerCompleteDTO(String workerNotes) {
        this.workerNotes = workerNotes;
    }

    public String getWorkerNotes() {
        return workerNotes;
    }

    public void setWorkerNotes(String workerNotes) {
        this.workerNotes = workerNotes;
    }
}