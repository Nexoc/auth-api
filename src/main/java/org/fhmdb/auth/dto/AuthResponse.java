package org.fhmdb.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String token,
        Integer userId,
        String email,
        String name,
        boolean oauthUser,
        Long faceId
) {}
