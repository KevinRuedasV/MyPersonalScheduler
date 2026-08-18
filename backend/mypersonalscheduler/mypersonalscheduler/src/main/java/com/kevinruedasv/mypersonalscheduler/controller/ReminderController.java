package com.kevinruedasv.mypersonalscheduler.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kevinruedasv.mypersonalscheduler.dto.CreateReminderRequest;
import com.kevinruedasv.mypersonalscheduler.dto.ReminderResponse;
import com.kevinruedasv.mypersonalscheduler.exception.InvalidReminderDateTimeException;
import com.kevinruedasv.mypersonalscheduler.model.Reminder;
import com.kevinruedasv.mypersonalscheduler.service.ReminderService;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(
            ReminderService reminderService
    ) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            Authentication authentication,
            @RequestBody CreateReminderRequest request
    ) {
        String userId = authentication.getName();
        LocalDateTime reminderDateTime;

        try {
            reminderDateTime =
                    LocalDateTime.parse(request.getReminderDateTime());
        } catch (Exception exception) {
            throw new InvalidReminderDateTimeException(
                    "Reminder date and time must use the format "
                    + "yyyy-MM-ddTHH:mm:ss."
            );
        }

        Reminder reminder = reminderService.createReminder(
                userId,
                request.getNoteId(),
                request.getMessage(),
                reminderDateTime
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(reminder));
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getReminders(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(
                reminderService.getRemindersByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<ReminderResponse>> getDeliveredReminders(
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(
                reminderService.getDeliveredReminders(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(
            Authentication authentication,
            @PathVariable String reminderId
    ) {
        String userId = authentication.getName();
        reminderService.deleteReminder(userId, reminderId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllReminders(
                Authentication authentication
    ) {
        String userId = authentication.getName();
        reminderService.deleteAllReminders(userId);

        return ResponseEntity.noContent().build();
    }

    private ReminderResponse toResponse(
            Reminder reminder
    ) {
        ReminderResponse response = new ReminderResponse();

        response.setReminderId(reminder.getReminderId());
        response.setNoteId(reminder.getNoteId());
        response.setMessage(reminder.getMessage());
        response.setReminderDateTime(
                reminder.getReminderDateTime()
        );
        response.setStatus(
                reminder.getStatus() != null
                        ? reminder.getStatus().name()
                        : null
        );
        response.setCreatedAt(reminder.getCreatedAt());

        return response;
    }
}