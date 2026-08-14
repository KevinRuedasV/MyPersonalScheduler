package com.kevinruedasv.mypersonalscheduler.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String noteId) {
        super("Note with id '" + noteId + "' was not found.");
    }
}