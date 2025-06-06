package org.fhmdb.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        String name,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 5, message = "Password must be at least 5 characters")
        String password
) {}
