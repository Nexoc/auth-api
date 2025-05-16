package org.fhmdb.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "Marat Davletshin")
        @NotBlank(message = "The name field can't be blank")
        String name,
        @Schema(example = "marat@example.com")
        @NotBlank(message = "The email field can't be blank")
        @Email(message = "Please enter email in proper format")
        String email,
        @Schema(example = "strongPassword123")
        @NotBlank(message = "The password field can't be blank")
        @Size(min = 5, message = "The password must be at least 5 characters")
        String password
) {}
