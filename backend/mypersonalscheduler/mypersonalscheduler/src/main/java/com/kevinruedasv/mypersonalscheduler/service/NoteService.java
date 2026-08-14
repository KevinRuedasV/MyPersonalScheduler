package com.kevinruedasv.mypersonalscheduler.service;

import java.time.LocalDate;
import java.util.List;

import com.kevinruedasv.mypersonalscheduler.model.Note;

public interface NoteService {

    Note createNote(
            String userId,
            String title,
            String content,
            List<String> tags
    );

    Note getNoteById(
            String userId,
            String noteId
    );

    List<Note> getNotesByUserId(
            String userId
    );

    List<Note> searchNotes(
            String userId,
            String searchTerm
    );

    List<Note> filterNotesByTag(
            String userId,
            String tag
    );

    Note updateNote(
            String userId,
            String noteId,
            String title,
            String content,
            List<String> tags
    );

    void deleteNote(
            String userId,
            String noteId
    );

    Note convertToTask(
            String userId,
            String noteId,
            LocalDate dueDate
    );

    Note convertToEvent(
            String userId,
            String noteId,
            LocalDate eventDate
    );

    Note completeTask(
            String userId,
            String noteId
    );

    Note celebrateEvent(
            String userId,
            String noteId
    );
}