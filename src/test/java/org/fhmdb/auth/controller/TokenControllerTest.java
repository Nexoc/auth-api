package org.fhmdb.auth.controller;

import org.fhmdb.auth.model.User;
import org.fhmdb.auth.repository.UserRepository;
import org.fhmdb.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TokenControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = null; // not used in controller
        UserRepository userRepository = null; // not used in controller
        TokenController controller = new TokenController(jwtUtil, userRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    private Authentication authenticatedUser() {
        User user = User.builder()
                .userId(1)
                .name("Test User")
                .build();
        return new TestingAuthenticationToken(user, null, "ROLE_USER");
    }

    @Test
    void validateToken_ShouldReturnUserInfo_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/auth/validate").principal(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")));
    }

    @Test
    void validateToken_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }
}
