package org.fhmdb.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "The name field can't be blank")
        String name,
        @NotBlank(message = "The email field can't be blank")
        @Email(message = "Please enter email in proper format")
        String email,
        @NotBlank(message = "The password field can't be blank")
        @Size(min = 5, message = "The password must be at least 5 characters")
        String password
) {}
