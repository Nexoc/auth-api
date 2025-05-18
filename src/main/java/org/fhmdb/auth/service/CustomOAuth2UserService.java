package org.fhmdb.auth.service;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        // GitHub may not provide the email in the initial user object
        String email = (String) user.getAttributes().get("email");

        if (email == null) {
            // Fetch email from GitHub's separate /user/emails endpoint
            String token = userRequest.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            for (Map<String, Object> mailEntry : response.getBody()) {
                if ((Boolean) mailEntry.get("primary") && (Boolean) mailEntry.get("verified")) {
                    email = (String) mailEntry.get("email");
                    break;
                }
            }

            if (email == null) {
                throw new OAuth2AuthenticationException("Email not available from GitHub.");
            }

            // Manually add the email to user attributes
            Map<String, Object> attributes = new HashMap<>(user.getAttributes());
            attributes.put("email", email);

            return new DefaultOAuth2User(user.getAuthorities(), attributes, "login");
        }

        return user;
    }
}
