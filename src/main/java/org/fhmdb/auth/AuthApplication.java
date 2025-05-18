package org.fhmdb.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.fhmdb.auth.config.CorsProperties;
import org.fhmdb.auth.config.FrontendProperties;
import org.fhmdb.auth.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@OpenAPIDefinition(info = @Info(title = "Auth API", version = "v1"))
@SpringBootApplication
@EnableConfigurationProperties({
		JwtProperties.class,
		CorsProperties.class,
		FrontendProperties.class})

public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
