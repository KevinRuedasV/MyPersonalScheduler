package com.kevinruedasv.mypersonalscheduler.model;

import java.time.LocalDate;

public class Task extends Note {

    private LocalDate dueDate;

    private TaskStatus status;

    public Task() {
        super();
        this.status = TaskStatus.PENDING;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}