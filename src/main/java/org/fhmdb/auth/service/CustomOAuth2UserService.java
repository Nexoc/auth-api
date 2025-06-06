package org.fhmdb.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${github.user-emails-url}")
    private String githubUserEmailsUrl;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = (String) user.getAttributes().get("email");

        if (email == null) {
            logger.warn("GitHub email not provided in main user object, fetching manually...");

            String token = userRequest.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    githubUserEmailsUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            for (Map<String, Object> mailEntry : response.getBody()) {
                logger.debug("Found email: {}", mailEntry);
                if ((Boolean) mailEntry.get("primary") && (Boolean) mailEntry.get("verified")) {
                    email = (String) mailEntry.get("email");
                    break;
                }
            }

            if (email == null) {
                logger.error("No verified primary email found from GitHub /user/emails");
                throw new OAuth2AuthenticationException("Email not available from GitHub.");
            }

            logger.info("Successfully retrieved email from GitHub: {}", email);

            Map<String, Object> attributes = new HashMap<>(user.getAttributes());
            attributes.put("email", email);

            return new DefaultOAuth2User(user.getAuthorities(), attributes, "login");
        }

        logger.info("GitHub OAuth login with email: {}", email);

        return user;
    }
}
