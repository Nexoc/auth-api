package org.fhmdb.auth.service;


import lombok.RequiredArgsConstructor;
import org.fhmdb.auth.dto.AuthResponse;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.security.JwtUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthResponseBuilder {

    private final JwtUtil jwtUtil;

    public AuthResponse build(User user) {
        String token = jwtUtil.generateToken(user.getEmail() != null ? user.getEmail() : "anonymous");

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getEmail(),                // может быть null → Jackson сам отобразит как null
                user.getName() != null ? user.getName() : "Unknown", // дефолтное значение
                user.isOauthUser(),
                user.getFaceId()
        );
    }
}
