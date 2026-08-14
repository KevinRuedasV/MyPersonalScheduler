package com.kevinruedasv.mypersonalscheduler.repository;

import com.kevinruedasv.mypersonalscheduler.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    
}