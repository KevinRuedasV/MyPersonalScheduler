package com.kevinruedasv.mypersonalscheduler.dto;

public class LoginResponse {

    private UserResponse user;

    public LoginResponse() {
    }

    public LoginResponse(UserResponse user) {
        this.user = user;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}