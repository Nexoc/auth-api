package org.fhmdb.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhmdb.auth.dto.LoginRequest;
import org.fhmdb.auth.dto.RegisterRequest;
import org.fhmdb.auth.exceptions.GlobalExceptionHandler;
import org.fhmdb.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_ShouldReturnSuccessMessage() throws Exception {
        RegisterRequest request = new RegisterRequest("Max", "max@example.com", "pass123");
        when(authService.register(any())).thenReturn("User registered");

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("Max", "", "pass123");

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        LoginRequest loginRequest = new LoginRequest("max@example.com", "pass123");
        when(authService.login("max@example.com", "pass123")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        LoginRequest invalidLogin = new LoginRequest("", "pass123");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnError_WhenCredentialsInvalid() throws Exception {
        LoginRequest wrongLogin = new LoginRequest("wrong@example.com", "wrongpass");
        when(authService.login(wrongLogin.email(), wrongLogin.password()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(wrongLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials")));
    }
}
