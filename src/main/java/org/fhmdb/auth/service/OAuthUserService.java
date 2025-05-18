package org.fhmdb.auth.service;

import lombok.RequiredArgsConstructor;
import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthUserService {

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

        return existingUser.orElseGet(() -> {
            String randomPassword = UUID.randomUUID().toString(); // generate random password
            String encodedPassword = passwordEncoder.encode(randomPassword); // encode it

            User newUser = User.builder()
                    .email(email)
                    .name(name != null ? name : "GitHub User")
                    .password(encodedPassword)
                    .provider("github")
                    .build();

            return userRepository.save(newUser);
        });
    }
}
