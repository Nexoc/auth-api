package org.fhmdb.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.security.JwtUtil;
import org.fhmdb.auth.service.OAuthUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final OAuthUserService oAuthUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // Extract OAuth2 user from the authentication principal
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user's email and name from OAuth2 attributes
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.debug("OAuth2 login success: email={}, name={}", email, name);

        // If email is missing, return error response
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider.");
            return;
        }

        // Register or fetch existing user using service
        User user = oAuthUserService.findOrRegister(email, name);

        // Generate JWT token for the user
        String token = jwtUtil.generateToken(user.getEmail());

        log.debug("JWT generated for {}: {}", user.getEmail(), token);

        // Construct a frontend redirect URL with query parameters
        // This includes the generated JWT token, the user's ID, and their email (encoded for safe URL usage)
        String redirectUrl = String.format(
                "http://localhost:8080/oauth-success?token=%s&id=%d&username=%s",
                token,                                         // JWT token for authentication
                user.getUserId(),                              // User's internal database ID
                URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8) // Email (username) safely encoded
        );

        // Send an HTTP redirect to the frontend
        // This tells the browser to go to the given URL, where the frontend can extract the token and user info
        response.sendRedirect(redirectUrl);


        response.sendRedirect(redirectUrl);

    }
}
