package com.kevinruedasv.mypersonalscheduler.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.kevinruedasv.mypersonalscheduler.dto.CreateUserRequest;
import com.kevinruedasv.mypersonalscheduler.dto.LoginRequest;
import com.kevinruedasv.mypersonalscheduler.dto.LoginResponse;
import com.kevinruedasv.mypersonalscheduler.dto.UpdateUserRequest;
import com.kevinruedasv.mypersonalscheduler.dto.UserResponse;
import com.kevinruedasv.mypersonalscheduler.model.User;
import com.kevinruedasv.mypersonalscheduler.service.JwtService;
import com.kevinruedasv.mypersonalscheduler.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

        private final UserService userService;

        private final JwtService jwtService;

        public UserController(
                UserService userService,
                JwtService jwtService
        ) {
        this.userService = userService;
        this.jwtService = jwtService;
        }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody CreateUserRequest request
    ) {
        User user = userService.register(
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(user));
    }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(
                @RequestBody LoginRequest request
        ) {
        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                new LoginResponse(
                        toResponse(user),
                        accessToken
                )
        );
        }

        @GetMapping("/{userId}")
        public ResponseEntity<UserResponse> getUser(
                Authentication authentication,
                @PathVariable String userId
        ) {
        String authenticatedUserId = authentication.getName();

        if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getUserById(userId);

        return ResponseEntity.ok(toResponse(user));
        }

        @PutMapping("/{userId}")
        public ResponseEntity<UserResponse> updateUsername(
                Authentication authentication,
                @PathVariable String userId,
                @RequestBody UpdateUserRequest request
        ) {
        String authenticatedUserId = authentication.getName();

        if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.updateUsername(
                userId,
                request.getUsername()
        );

        return ResponseEntity.ok(toResponse(user));
        }

        @DeleteMapping("/{userId}")
        public ResponseEntity<Void> deleteUser(
                Authentication authentication,
                @PathVariable String userId
        ) {
        String authenticatedUserId = authentication.getName();

        if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
        }

    private UserResponse toResponse(
            User user
    ) {
        UserResponse response = new UserResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}