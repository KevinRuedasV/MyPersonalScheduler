package com.kevinruedasv.mypersonalscheduler.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

    private final ReminderService reminderService;

    public ReminderScheduler(
            ReminderService reminderService
    ) {
        this.reminderService = reminderService;
    }

    @Scheduled(fixedRate = 30000)
    public void processPendingReminders() {
        reminderService.deliverPendingReminders();
    }
}