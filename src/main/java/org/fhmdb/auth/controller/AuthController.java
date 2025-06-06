package org.fhmdb.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fhmdb.auth.dto.RegisterRequest;
import org.fhmdb.auth.dto.UpdateProfileRequest;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.fhmdb.auth.dto.LoginRequest;

import java.util.Optional;

@Tag(name = "auth-controller")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }


    @Operation(summary = "registration")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();

        String result = authService.register(user);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(token);
    }


    @PutMapping("/profile")
    @Operation(summary = "Update your own profile")
    public ResponseEntity<String> updateOwnProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        authService.updateProfile(currentUser, request);
        return ResponseEntity.ok("Profile updated successfully");
    }



    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = authService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


/*
    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        boolean deleted = authService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
 */

}
