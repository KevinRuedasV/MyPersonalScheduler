package com.kevinruedasv.mypersonalscheduler.dto;

public class CreateReminderRequest {

    private String noteId;

    private String message;

    private String reminderDateTime;

    public CreateReminderRequest() {
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(String reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }
}