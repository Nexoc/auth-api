package org.fhmdb.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhmdb.auth.config.FrontendProperties;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.service.OAuthUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final OAuthUserService oAuthUserService;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // Extract authenticated OAuth2 user
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract required user information
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.debug("OAuth2 login success: email={}, name={}", email, name);

        // Validate email presence
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider.");
            return;
        }

        // Find or create user in the system
        User user = oAuthUserService.findOrRegister(email, name);

        // Generate JWT token for the user
        String token = jwtUtil.generateToken(user.getEmail());

        log.debug("Generated JWT token for {}: {}", user.getEmail(), token);

        // Log frontend URL value
        log.debug("Redirecting to frontend: {}", frontendProperties.getUrl());

        // Build frontend redirect URL with token and user info
        // Use URI from application.yml
        String redirectUrl = String.format(
                "%s?token=%s&id=%d&username=%s",
                frontendProperties.getUrl(),  // или getUrl() — в зависимости от выбранного варианта
                token,
                user.getUserId(),
                URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
        );

        // Redirect to frontend with token and user data
        response.sendRedirect(redirectUrl);
    }
}
