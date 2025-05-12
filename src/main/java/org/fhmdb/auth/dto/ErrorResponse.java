package org.fhmdb.auth.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int code;
    private String message;

    public ErrorResponse(int code, String message) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.message = message;
    }
}
