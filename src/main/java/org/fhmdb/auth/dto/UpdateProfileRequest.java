package org.fhmdb.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@JacksonXmlRootElement(localName = "updateProfileRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

        private String name;

        @Email(message = "Invalid email format")
        private String email;

        @Size(min = 5, message = "Password must be at least 5 characters")
        private String password;
}
