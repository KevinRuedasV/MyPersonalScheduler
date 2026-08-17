package com.kevinruedasv.mypersonalscheduler.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    private static final String USER_ID_HEADER = "X-User-Id";

    private final ReminderService reminderService;

    public ReminderController(
            ReminderService reminderService
    ) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody CreateReminderRequest request
    ) {
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
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        return ResponseEntity.ok(
                reminderService.getRemindersByUserId(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<ReminderResponse>> getDeliveredReminders(
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        return ResponseEntity.ok(
                reminderService.getDeliveredReminders(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(
            @RequestHeader(USER_ID_HEADER) String userId,
            @PathVariable String reminderId
    ) {
        reminderService.deleteReminder(userId, reminderId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllReminders(
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
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