package org.fhmdb.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.fhmdb.auth.dto.AuthResponse;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.security.JwtUtil;
import org.fhmdb.auth.service.AuthResponseBuilder;
import org.fhmdb.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthResponseBuilder authResponseBuilder;

    public TokenController(JwtUtil jwtUtil, UserRepository userRepository, AuthResponseBuilder authResponseBuilder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authResponseBuilder = authResponseBuilder;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Validate token and return user details")
    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();
        AuthResponse response = authResponseBuilder.build(user);
        return ResponseEntity.ok(response);
    }
}
