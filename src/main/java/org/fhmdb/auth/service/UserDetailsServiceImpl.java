package org.fhmdb.auth.service;

import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting login for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        if (user.isOauthUser()) {
            logger.warn("Blocked password login for OAuth user: {}", email);
            throw new BadCredentialsException("This account is linked with GitHub. Please use GitHub login.");
        }

        logger.info("Login successful for user: {}", email);
        return user;
    }


    public UserDetails loadUserByEmailWithoutOauthCheck(String email) throws UsernameNotFoundException {
        logger.info("Loading user (token validation) for email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
