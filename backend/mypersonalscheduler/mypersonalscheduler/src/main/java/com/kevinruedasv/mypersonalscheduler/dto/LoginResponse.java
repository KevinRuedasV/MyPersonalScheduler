package com.kevinruedasv.mypersonalscheduler.dto;

public class LoginResponse {

    private UserResponse user;

    private String accessToken;

    public LoginResponse() {
    }

    public LoginResponse(
            UserResponse user,
            String accessToken
    ) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}