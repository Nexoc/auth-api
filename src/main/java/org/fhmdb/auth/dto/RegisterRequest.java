package org.fhmdb.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "registerRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

        @Schema(example = "Marat Davletshin")
        @NotBlank(message = "The name field can't be blank")
        private String name;

        @Schema(example = "marat@example.com")
        @NotBlank(message = "The email field can't be blank")
        @Email(message = "Please enter email in proper format")
        private String email;

        @Schema(example = "strongPassword123")
        @NotBlank(message = "The password field can't be blank")
        @Size(min = 5, message = "The password must be at least 5 characters")
        private String password;
}
