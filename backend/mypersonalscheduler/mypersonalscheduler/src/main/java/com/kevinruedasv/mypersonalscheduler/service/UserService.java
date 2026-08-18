package com.kevinruedasv.mypersonalscheduler.service;

import com.kevinruedasv.mypersonalscheduler.model.User;

public interface UserService {

    User register(
            String email,
            String username,
            String password
    );

    User login(
            String email,
            String password
    );

    User getUserById(
            String userId
    );
    
    User updateUsername(
            String userId,
            String username
    );

    void deleteUser(
            String userId
    );
}