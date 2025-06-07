package org.fhmdb.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JacksonXmlRootElement(localName = "error")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
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
