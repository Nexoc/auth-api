package org.fhmdb.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fhmdb.auth.dto.AuthResponse;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.service.AuthService;
import org.fhmdb.auth.service.AuthResponseBuilder;
import org.fhmdb.auth.service.FaceAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Tag(name = "auth-face-controller")
@RestController
@RequestMapping("/auth")
public class FaceAuthController {

    private final AuthService authService;
    private final FaceAuthService faceAuthService;
    private final AuthResponseBuilder authResponseBuilder;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public FaceAuthController(AuthService authService,
                              FaceAuthService faceAuthService,
                              AuthResponseBuilder authResponseBuilder) {
        this.authService = authService;
        this.faceAuthService = faceAuthService;
        this.authResponseBuilder = authResponseBuilder;
    }

    @Operation(summary = "Register or login using photos (face recognition)")
    @PostMapping(value = "/register-or-login/face", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> registerOrLoginWithFace(@RequestPart("photos") List<MultipartFile> photos) throws IOException {
        log.info("Received {} photos for face recognition", photos.size());

        Long faceId = faceAuthService.recognizeFaceAndGetId(photos);
        log.info("Recognized faceId: {}", faceId);

        // Check if user exists
        Optional<User> userOpt = faceAuthService.getUserByFaceId(faceId);

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("User found for faceId {}: {}", faceId, user.getEmail());
        } else {
            log.info("No user found for faceId {}. Creating new user...", faceId);
            user = User.builder()
                    .name("FaceUser_" + faceId)
                    .email("faceuser" + faceId + "@local")
                    .password("") // Empty password
                    .faceId(faceId)
                    .build();
            user = authService.register(user);
            log.info("New user registered with email: {}", user.getEmail());
        }

        // Build structured response
        AuthResponse response = authResponseBuilder.build(user);

        return ResponseEntity.ok(response);
    }

}
