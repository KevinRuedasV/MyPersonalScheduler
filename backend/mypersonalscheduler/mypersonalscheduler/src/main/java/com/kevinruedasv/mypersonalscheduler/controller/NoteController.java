package com.kevinruedasv.mypersonalscheduler.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kevinruedasv.mypersonalscheduler.dto.CreateNoteRequest;
import com.kevinruedasv.mypersonalscheduler.dto.DateRequest;
import com.kevinruedasv.mypersonalscheduler.dto.NoteResponse;
import com.kevinruedasv.mypersonalscheduler.dto.UpdateNoteRequest;
import com.kevinruedasv.mypersonalscheduler.exception.InvalidRequestException;
import com.kevinruedasv.mypersonalscheduler.model.Event;
import com.kevinruedasv.mypersonalscheduler.model.Note;
import com.kevinruedasv.mypersonalscheduler.model.Task;
import com.kevinruedasv.mypersonalscheduler.service.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            Authentication authentication,
            @RequestBody CreateNoteRequest request
    ) {
        String userId = authentication.getName();
        Note note = noteService.createNote(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getTags()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(note));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(
            Authentication authentication,
            @PathVariable String noteId
    ) {
        String userId = authentication.getName();
        Note note = noteService.getNoteById(userId, noteId);

        return ResponseEntity.ok(toResponse(note));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag
    ) {
        List<Note> notes;
        String userId = authentication.getName();

        if (search != null && tag != null) {
            throw new InvalidRequestException(
                    "Search and tag filters cannot be used simultaneously."
            );
        }

        if (search != null) {
            notes = noteService.searchNotes(userId, search);
        } else if (tag != null) {
            notes = noteService.filterNotesByTag(userId, tag);
        } else {
            notes = noteService.getNotesByUserId(userId);
        }

        return ResponseEntity.ok(
                notes.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            Authentication authentication,
            @PathVariable String noteId,
            @RequestBody UpdateNoteRequest request
    ) {
        String userId = authentication.getName();
        Note note = noteService.updateNote(
                userId,
                noteId,
                request.getTitle(),
                request.getContent(),
                request.getTags()
        );

        return ResponseEntity.ok(toResponse(note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            Authentication authentication,
            @PathVariable String noteId
    ) {
        String userId = authentication.getName();
        noteService.deleteNote(userId, noteId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{noteId}/task")
    public ResponseEntity<NoteResponse> convertToTask(
            Authentication authentication,
            @PathVariable String noteId,
            @RequestBody DateRequest request
    ) {
        String userId = authentication.getName();
        Note note = noteService.convertToTask(
                userId,
                noteId,
                LocalDate.parse(request.getDate())
        );

        return ResponseEntity.ok(toResponse(note));
    }

    @PostMapping("/{noteId}/event")
    public ResponseEntity<NoteResponse> convertToEvent(
            Authentication authentication,
            @PathVariable String noteId,
            @RequestBody DateRequest request
    ) {
        String userId = authentication.getName();
        Note note = noteService.convertToEvent(
                userId,
                noteId,
                LocalDate.parse(request.getDate())
        );

        return ResponseEntity.ok(toResponse(note));
    }

    @PostMapping("/{noteId}/complete")
    public ResponseEntity<NoteResponse> completeTask(
            Authentication authentication,
            @PathVariable String noteId
    ) {
        String userId = authentication.getName();
        Note note = noteService.completeTask(userId, noteId);

        return ResponseEntity.ok(toResponse(note));
    }

    @PostMapping("/{noteId}/celebrate")
    public ResponseEntity<NoteResponse> celebrateEvent(
            Authentication authentication,
            @PathVariable String noteId
    ) {
        String userId = authentication.getName();
        Note note = noteService.celebrateEvent(userId, noteId);

        return ResponseEntity.ok(toResponse(note));
    }

    private NoteResponse toResponse(Note note) {

        NoteResponse response = new NoteResponse();

        response.setNoteId(note.getNoteId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setTags(note.getTags());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());

        if (note instanceof Task task) {

            response.setType("TASK");
            response.setDate(task.getDueDate());
            response.setStatus(
                    task.getStatus() != null
                            ? task.getStatus().name()
                            : null
            );

        } else if (note instanceof Event event) {

            response.setType("EVENT");
            response.setDate(event.getEventDate());
            response.setStatus(
                    event.getStatus() != null
                            ? event.getStatus().name()
                            : null
            );

        } else {

            response.setType("NOTE");
            response.setDate(null);
            response.setStatus(null);
        }

        return response;
    }
}