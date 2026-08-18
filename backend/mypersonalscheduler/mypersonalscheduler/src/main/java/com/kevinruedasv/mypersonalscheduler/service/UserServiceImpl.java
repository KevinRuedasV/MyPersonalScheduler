package com.kevinruedasv.mypersonalscheduler.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kevinruedasv.mypersonalscheduler.exception.InvalidCredentialsException;
import com.kevinruedasv.mypersonalscheduler.exception.InvalidUserException;
import com.kevinruedasv.mypersonalscheduler.exception.UserAlreadyExistsException;
import com.kevinruedasv.mypersonalscheduler.exception.UserNotFoundException;
import com.kevinruedasv.mypersonalscheduler.model.User;
import com.kevinruedasv.mypersonalscheduler.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(
            String email,
            String username,
            String password
    ) {
        validateEmail(email);
        validatePassword(password);

        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException(normalizedEmail);
        }

        String finalUsername;

        if (username == null || username.isBlank()) {
            finalUsername = normalizedEmail;
        } else {
            finalUsername = username.trim();
        }

        Instant now = Instant.now();

        User user = new User();

        user.setUsername(finalUsername);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }

    @Override
    public User login(
            String email,
            String password
    ) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new InvalidCredentialsException();
        }

        String normalizedIdentifier = email.trim();

        User user = userRepository
                    .findByEmail(normalizeEmail(normalizedIdentifier))
                    .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    @Override
    public User getUserById(
            String userId
    ) {
        validateUserId(userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User updateUsername(
            String userId,
            String username
    ) {
        validateUserId(userId);

        if (username == null || username.isBlank()) {
            throw new InvalidUserException(
                    "Username cannot be null or blank."
            );
        }

        User user = getUserById(userId);

        user.setUsername(username.trim());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(
            String userId
    ) {
        validateUserId(userId);

        User user = getUserById(userId);

        userRepository.delete(user);
    }


    private void validateUserId(
            String userId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidUserException(
                    "User id cannot be null or blank."
            );
        }
    }

private void validateEmail(
        String email
) {
    if (email == null || email.isBlank()) {
        throw new InvalidUserException(
                "Email is mandatory."
        );
    }

    String normalizedEmail = normalizeEmail(email);

    if (!normalizedEmail.matches(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )) {
        throw new InvalidUserException(
                "Email format is invalid."
        );
    }
}

    private void validatePassword(
            String password
    ) {
        if (password == null || password.isBlank()) {
            throw new InvalidUserException(
                    "Password is mandatory."
            );
        }
    }

    private String normalizeEmail(
            String email
    ) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}