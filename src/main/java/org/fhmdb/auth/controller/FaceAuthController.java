package org.fhmdb.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.service.FaceAuthService;
import org.fhmdb.auth.service.AuthService;
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

    public FaceAuthController(AuthService authService, FaceAuthService faceAuthService) {
        this.authService = authService;
        this.faceAuthService = faceAuthService;
    }

    @Operation(summary = "Register or login using photos (face recognition)")
    @PostMapping(value = "/register-or-login/face", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerOrLoginWithFace(@RequestPart("photos") List<MultipartFile> photos) throws IOException {
        Long faceId = faceAuthService.recognizeFaceAndGetId(photos);

        // Проверяем, есть ли уже пользователь с таким faceId
        Optional<User> userOpt = faceAuthService.getUserByFaceId(faceId);

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // Регистрируем нового пользователя
            user = User.builder()
                    .name("FaceUser_" + faceId)
                    .email("faceuser" + faceId + "@local")
                    .password("")
                    .faceId(faceId)
                    .build();
            authService.register(user);
        }

        // Возвращаем JWT
        String token = faceAuthService.generateToken(user);
        return ResponseEntity.ok(token);
    }

}
