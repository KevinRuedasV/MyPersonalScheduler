package com.kevinruedasv.mypersonalscheduler.service;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kevinruedasv.mypersonalscheduler.exception.InvalidReminderException;
import com.kevinruedasv.mypersonalscheduler.exception.NoteNotFoundException;
import com.kevinruedasv.mypersonalscheduler.exception.ReminderNotFoundException;
import com.kevinruedasv.mypersonalscheduler.model.Note;
import com.kevinruedasv.mypersonalscheduler.model.Reminder;
import com.kevinruedasv.mypersonalscheduler.model.ReminderStatus;
import com.kevinruedasv.mypersonalscheduler.repository.NoteRepository;
import com.kevinruedasv.mypersonalscheduler.repository.ReminderRepository;

@Service
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    private final NoteRepository noteRepository;

    public ReminderServiceImpl(
            ReminderRepository reminderRepository,
            NoteRepository noteRepository
    ) {
        this.reminderRepository = reminderRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public Reminder createReminder(
            String userId,
            String noteId,
            String message,
            LocalDateTime reminderDateTime
    ) {
        validateUserId(userId);
        validateNoteId(noteId);
        validateMessage(message);
        validateReminderDateTime(reminderDateTime);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        if (!userId.equals(note.getUserId())) {
            throw new NoteNotFoundException(noteId);
        }

        Reminder reminder = new Reminder();

        reminder.setUserId(userId);
        reminder.setNoteId(noteId);
        reminder.setMessage(message.trim());
        reminder.setReminderDateTime(reminderDateTime);
        reminder.setStatus(ReminderStatus.PENDING);
        reminder.setCreatedAt(Instant.now());

        return reminderRepository.save(reminder);
    }

    @Override
    public List<Reminder> getRemindersByUserId(
            String userId
    ) {
        validateUserId(userId);

        return reminderRepository
                .findByUserIdOrderByReminderDateTimeAsc(userId);
    }

    @Override
    public List<Reminder> getDeliveredReminders(
            String userId
    ) {
        validateUserId(userId);

        return reminderRepository
                .findByUserIdAndStatusOrderByReminderDateTimeAsc(
                        userId,
                        ReminderStatus.DELIVERED
                );
    }

    @Override
    public void deleteReminder(
            String userId,
            String reminderId
    ) {
        validateUserId(userId);
        validateReminderId(reminderId);

        Reminder reminder = reminderRepository
                .findById(reminderId)
                .orElseThrow(
                        () -> new ReminderNotFoundException(reminderId)
                );

        if (!userId.equals(reminder.getUserId())) {
            throw new ReminderNotFoundException(reminderId);
        }

        reminderRepository.delete(reminder);
    }

    @Override
    public void deleteAllReminders(
            String userId
    ) {
        validateUserId(userId);

        reminderRepository.deleteByUserId(userId);
    }

    @Override
    public void deliverPendingReminders() {

        LocalDateTime now = LocalDateTime.now();

        List<Reminder> pendingReminders =
                reminderRepository
                        .findByStatusAndReminderDateTimeLessThanEqual(
                                ReminderStatus.PENDING,
                                now
                        );

        for (Reminder reminder : pendingReminders) {

            reminder.setStatus(ReminderStatus.DELIVERED);

            reminderRepository.save(reminder);
        }
    }

    private void validateUserId(
            String userId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidReminderException(
                    "User id cannot be null or blank."
            );
        }
    }

    private void validateNoteId(
            String noteId
    ) {
        if (noteId == null || noteId.isBlank()) {
            throw new InvalidReminderException(
                    "Note id cannot be null or blank."
            );
        }
    }

    private void validateReminderId(
            String reminderId
    ) {
        if (reminderId == null || reminderId.isBlank()) {
            throw new InvalidReminderException(
                    "Reminder id cannot be null or blank."
            );
        }
    }

    private void validateMessage(
            String message
    ) {
        if (message == null || message.isBlank()) {
            throw new InvalidReminderException(
                    "Reminder message is mandatory."
            );
        }
    }

    private void validateReminderDateTime(
            LocalDateTime reminderDateTime
    ) {
        if (reminderDateTime == null) {
            throw new InvalidReminderException(
                    "Reminder date and time are mandatory."
            );
        }
    }
}