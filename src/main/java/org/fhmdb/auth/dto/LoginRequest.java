package org.fhmdb.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest(
        @Schema(example = "marat@example.com")
        @NotBlank String email,
        @Schema(example = "strongPassword123")
        @NotBlank String password
) {}