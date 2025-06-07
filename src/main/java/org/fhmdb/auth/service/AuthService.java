package org.fhmdb.auth.service;

import org.fhmdb.auth.dto.AuthResponse;
import org.fhmdb.auth.dto.UpdateProfileRequest;
import org.fhmdb.auth.exceptions.EmailAlreadyExistsException;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.fhmdb.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Attempt to register with existing email: {}", user.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        logger.info("User registered: {}", user.getEmail());
        return savedUser;
    }


    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed: email not found ({})", email);
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: invalid password for {}", email);
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User logged in: {}", email);
        return new AuthResponseBuilder(jwtUtil).build(user);
    }


    public User updateProfile(User currentUser, UpdateProfileRequest request) {
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Email already in use during profile update: {}", request.getEmail());
                throw new EmailAlreadyExistsException("Email already in use");
            }
            logger.info("User {} changed email to {}", currentUser.getUserId(), request.getEmail());
            currentUser.setEmail(request.getEmail());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            logger.info("User {} changed name to {}", currentUser.getUserId(), request.getName());
            currentUser.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            logger.info("User {} updated password", currentUser.getUserId());
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(currentUser);
    }
}
