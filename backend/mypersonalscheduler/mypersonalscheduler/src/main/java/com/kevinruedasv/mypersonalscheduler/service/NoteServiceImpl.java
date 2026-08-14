package com.kevinruedasv.mypersonalscheduler.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.kevinruedasv.mypersonalscheduler.exception.InvalidNoteException;
import com.kevinruedasv.mypersonalscheduler.exception.InvalidNoteStateException;
import com.kevinruedasv.mypersonalscheduler.exception.NoteNotFoundException;
import com.kevinruedasv.mypersonalscheduler.model.Event;
import com.kevinruedasv.mypersonalscheduler.model.EventStatus;
import com.kevinruedasv.mypersonalscheduler.model.Note;
import com.kevinruedasv.mypersonalscheduler.model.Task;
import com.kevinruedasv.mypersonalscheduler.model.TaskStatus;
import com.kevinruedasv.mypersonalscheduler.repository.NoteRepository;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public Note createNote(
            String userId,
            String title,
            String content,
            List<String> tags
    ) {
        validateUserId(userId);
        validateTitle(title);

        Instant now = Instant.now();

        Note note = new Note();

        note.setUserId(userId);
        note.setTitle(title.trim());
        note.setContent(content);
        note.setTags(normalizeTags(tags));
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        return noteRepository.save(note);
    }

    @Override
    public Note getNoteById(
            String userId,
            String noteId
    ) {
        validateUserId(userId);
        validateNoteId(noteId);

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        return note;
    }

    @Override
    public List<Note> getNotesByUserId(
            String userId
    ) {
        validateUserId(userId);

        return noteRepository.findByUserId(userId);
    }

    @Override
    public List<Note> searchNotes(
            String userId,
            String searchTerm
    ) {
        validateUserId(userId);

        if (searchTerm == null || searchTerm.isBlank()) {
            return getNotesByUserId(userId);
        }

        String normalizedSearchTerm =
                searchTerm.trim().toLowerCase(Locale.ROOT);

        return noteRepository.findByUserId(userId)
                .stream()
                .filter(note -> containsIgnoreCase(
                        note.getTitle(),
                        normalizedSearchTerm
                ) || containsIgnoreCase(
                        note.getContent(),
                        normalizedSearchTerm
                ))
                .toList();
    }

    @Override
    public List<Note> filterNotesByTag(
            String userId,
            String tag
    ) {
        validateUserId(userId);

        if (tag == null || tag.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedTag = tag.trim().toLowerCase(Locale.ROOT);

        return noteRepository.findByUserId(userId)
                .stream()
                .filter(note -> note.getTags() != null)
                .filter(note -> note.getTags()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(value -> value.trim().toLowerCase(Locale.ROOT))
                        .anyMatch(normalizedTag::equals))
                .toList();
    }

    @Override
    public Note updateNote(
            String userId,
            String noteId,
            String title,
            String content,
            List<String> tags
    ) {
        validateUserId(userId);
        validateNoteId(noteId);
        validateTitle(title);

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        if (isFinished(note)) {
            throw new InvalidNoteStateException(
                    "A completed or celebrated note cannot be modified."
            );
        }

        note.setTitle(title.trim());
        note.setContent(content);
        note.setTags(normalizeTags(tags));
        note.setUpdatedAt(Instant.now());

        return noteRepository.save(note);
    }

    @Override
    public void deleteNote(
            String userId,
            String noteId
    ) {
        validateUserId(userId);
        validateNoteId(noteId);

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        noteRepository.delete(note);
    }

    @Override
    public Note convertToTask(
            String userId,
            String noteId,
            LocalDate dueDate
    ) {
        validateUserId(userId);
        validateNoteId(noteId);
        validateDate(dueDate, "Due date");

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        if (note instanceof Task) {
            throw new InvalidNoteStateException(
                    "The note is already a task."
            );
        }

        if (note instanceof Event event
                && event.getStatus() == EventStatus.CELEBRATED) {
            throw new InvalidNoteStateException(
                    "A celebrated event cannot be converted into a task."
            );
        }

        if (note instanceof Event event
                && event.getStatus() == EventStatus.CANCELLED) {
            throw new InvalidNoteStateException(
                    "A cancelled event cannot be converted into a task."
            );
        }

        Task task = createTaskFromNote(note);

        task.setDueDate(dueDate);
        task.setStatus(TaskStatus.PENDING);
        task.setUpdatedAt(Instant.now());

        return noteRepository.save(task);
    }

    @Override
    public Note convertToEvent(
            String userId,
            String noteId,
            LocalDate eventDate
    ) {
        validateUserId(userId);
        validateNoteId(noteId);
        validateDate(eventDate, "Event date");

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        if (note instanceof Event) {
            throw new InvalidNoteStateException(
                    "The note is already an event."
            );
        }

        if (note instanceof Task task
                && task.getStatus() == TaskStatus.COMPLETED) {
            throw new InvalidNoteStateException(
                    "A completed task cannot be converted into an event."
            );
        }

        if (note instanceof Task task
                && task.getStatus() == TaskStatus.CANCELLED) {
            throw new InvalidNoteStateException(
                    "A cancelled task cannot be converted into an event."
            );
        }

        Event event = createEventFromNote(note);

        event.setEventDate(eventDate);
        event.setStatus(EventStatus.UPCOMING);
        event.setUpdatedAt(Instant.now());

        return noteRepository.save(event);
    }

    @Override
    public Note completeTask(
            String userId,
            String noteId
    ) {
        validateUserId(userId);
        validateNoteId(noteId);

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        if (!(note instanceof Task task)) {
            throw new InvalidNoteStateException(
                    "The note is not a task."
            );
        }

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new InvalidNoteStateException(
                    "The task is already completed."
            );
        }

        if (task.getStatus() == TaskStatus.CANCELLED) {
            throw new InvalidNoteStateException(
                    "A cancelled task cannot be completed."
            );
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setUpdatedAt(Instant.now());

        return noteRepository.save(task);
    }

    @Override
    public Note celebrateEvent(
            String userId,
            String noteId
    ) {
        validateUserId(userId);
        validateNoteId(noteId);

        Note note = findNoteById(noteId);

        validateOwnership(note, userId);

        if (!(note instanceof Event event)) {
            throw new InvalidNoteStateException(
                    "The note is not an event."
            );
        }

        if (event.getStatus() == EventStatus.CELEBRATED) {
            throw new InvalidNoteStateException(
                    "The event is already celebrated."
            );
        }

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new InvalidNoteStateException(
                    "A cancelled event cannot be celebrated."
            );
        }

        event.setStatus(EventStatus.CELEBRATED);
        event.setUpdatedAt(Instant.now());

        return noteRepository.save(event);
    }

    private Note findNoteById(String noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    private void validateOwnership(
            Note note,
            String userId
    ) {
        if (!userId.equals(note.getUserId())) {
            throw new NoteNotFoundException(note.getNoteId());
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidNoteException(
                    "User id cannot be null or blank."
            );
        }
    }

    private void validateNoteId(String noteId) {
        if (noteId == null || noteId.isBlank()) {
            throw new InvalidNoteException(
                    "Note id cannot be null or blank."
            );
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidNoteException(
                    "Note title is mandatory."
            );
        }
    }

    private void validateDate(
            LocalDate date,
            String fieldName
    ) {
        if (date == null) {
            throw new InvalidNoteException(
                    fieldName + " is mandatory."
            );
        }
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }

    private boolean containsIgnoreCase(
            String value,
            String searchTerm
    ) {
        return value != null
                && value.toLowerCase(Locale.ROOT)
                        .contains(searchTerm);
    }

    private boolean isFinished(Note note) {

        if (note instanceof Task task) {
            return task.getStatus() == TaskStatus.COMPLETED
                    || task.getStatus() == TaskStatus.CANCELLED;
        }

        if (note instanceof Event event) {
            return event.getStatus() == EventStatus.CELEBRATED
                    || event.getStatus() == EventStatus.CANCELLED;
        }

        return false;
    }

    private Task createTaskFromNote(Note note) {

        Task task = new Task();

        task.setNoteId(note.getNoteId());
        task.setUserId(note.getUserId());
        task.setTitle(note.getTitle());
        task.setContent(note.getContent());
        task.setTags(
                note.getTags() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(note.getTags())
        );
        task.setCreatedAt(note.getCreatedAt());

        return task;
    }

    private Event createEventFromNote(Note note) {

        Event event = new Event();

        event.setNoteId(note.getNoteId());
        event.setUserId(note.getUserId());
        event.setTitle(note.getTitle());
        event.setContent(note.getContent());
        event.setTags(
                note.getTags() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(note.getTags())
        );
        event.setCreatedAt(note.getCreatedAt());

        return event;
    }
}