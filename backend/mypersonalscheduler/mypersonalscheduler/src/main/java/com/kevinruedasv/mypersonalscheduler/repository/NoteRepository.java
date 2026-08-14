package com.kevinruedasv.mypersonalscheduler.repository;

import com.kevinruedasv.mypersonalscheduler.model.Note;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {

        List<Note> findByUserId(String userId);

}