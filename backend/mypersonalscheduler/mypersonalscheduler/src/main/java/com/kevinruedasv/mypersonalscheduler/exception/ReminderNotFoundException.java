package com.kevinruedasv.mypersonalscheduler.exception;

public class ReminderNotFoundException extends RuntimeException {

    public ReminderNotFoundException(String reminderId) {
        super("Reminder not found: " + reminderId);
    }
}