package org.fhmdb.auth.service;

import lombok.RequiredArgsConstructor;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthUserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthUserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Finds an existing user by email, or registers a new one.
     * @param email The user's email from OAuth provider.
     * @param name The user's name from OAuth provider.
     * @return User entity from database.
     */
    public User findOrRegister(String email, String name) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            logger.info("OAuth login: Existing user found with email {}", email);
            return existingUser.get();
        }

        logger.info("OAuth login: Registering new user with email {}", email);

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        User newUser = User.builder()
                .email(email)
                .name(name != null ? name : "GitHub User")
                .password(encodedPassword)
                .provider("github")
                .oauthUser(true)
                .build();

        User saved = userRepository.save(newUser);

        logger.info("New OAuth user registered: ID = {}, email = {}", saved.getUserId(), saved.getEmail());

        return saved;
    }
}
