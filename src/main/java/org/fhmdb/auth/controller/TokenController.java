package org.fhmdb.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.fhmdb.auth.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public TokenController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "validation")
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        User user = (User) authentication.getPrincipal();  // кастуем к твоему User
        return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "name", user.getName()
        ));
    }
}
