package org.fhmdb.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fhmdb.auth.dto.AuthResponse;
import org.fhmdb.auth.dto.LoginRequest;
import org.fhmdb.auth.dto.RegisterRequest;
import org.fhmdb.auth.dto.UpdateProfileRequest;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.service.AuthResponseBuilder;
import org.fhmdb.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "auth-controller")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final AuthResponseBuilder authResponseBuilder;

    public AuthController(AuthService authService, AuthResponseBuilder authResponseBuilder) {
        this.authService = authService;
        this.authResponseBuilder = authResponseBuilder;
    }

    /**
     * Registers a new user using name, email, and password.
     * Returns a structured AuthResponse with token and user info.
     */
    @Operation(summary = "User registration")
    @PostMapping(value = "/register",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getEmail());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User registeredUser = authService.register(user);
        AuthResponse response = authResponseBuilder.build(registeredUser);

        log.info("User registered successfully: {}", registeredUser.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Logs in an existing user with email and password.
     * Returns token and user info in a consistent AuthResponse format.
     */
    @Operation(summary = "User login with email and password")
    @PostMapping(
            value = "/login",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }


    /**
     * Updates the profile of the currently authenticated user.
     */
    @Operation(summary = "Update own profile")
    @PatchMapping(
            value = "/profile",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public ResponseEntity<AuthResponse> updateOwnProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        log.info("Updating profile for user ID: {}", currentUser.getUserId());

        User updatedUser = authService.updateProfile(currentUser, request);
        AuthResponse response = authResponseBuilder.build(updatedUser);
        return ResponseEntity.ok(response);
    }

}
