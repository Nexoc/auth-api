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

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            log.warn("OAuth2 login failed: email not provided by GitHub.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider.");
            return;
        }

        log.info("OAuth2 login success for GitHub user: email={}, name={}", email, name);

        User user = oAuthUserService.findOrRegister(email, name);
        log.info("User resolved or registered: id={}, email={}", user.getUserId(), user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Generated JWT token for {}: {}", user.getEmail(), token);  // Удали это позже по соображениям безопасности

        String redirectUrl = String.format(
                "%s?token=%s&id=%d&username=%s",
                frontendProperties.getUrl(),
                token,
                user.getUserId(),
                URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
        );

        log.info("Redirecting user to frontend with token: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
