package com.kevinruedasv.mypersonalscheduler.repository;

import com.kevinruedasv.mypersonalscheduler.model.User;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
}