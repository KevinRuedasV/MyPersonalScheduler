package com.kevinruedasv.mypersonalscheduler.service;

import java.time.LocalDateTime;
import java.util.List;

import com.kevinruedasv.mypersonalscheduler.model.Reminder;

public interface ReminderService {

    Reminder createReminder(
            String userId,
            String noteId,
            String message,
            LocalDateTime reminderDateTime
    );

    List<Reminder> getRemindersByUserId(
            String userId
    );

    List<Reminder> getDeliveredReminders(
            String userId
    );

    void deleteReminder(
            String userId,
            String reminderId
    );

    void deleteAllReminders(
            String userId
    );

    void deliverPendingReminders();
}