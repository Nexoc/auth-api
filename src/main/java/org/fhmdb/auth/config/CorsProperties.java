package org.fhmdb.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
@Component
public class CorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private boolean allowCredentials;

    @Value("${debug.enabled:false}")
    private boolean debug;

    @PostConstruct
    public void logCorsSettings() {
        if (debug) {
            System.out.println("=== CORS CONFIG ===");
            System.out.println("Allowed Origins: " + allowedOrigins);
            System.out.println("Allowed Methods: " + allowedMethods);
            System.out.println("Allowed Headers: " + allowedHeaders);
            System.out.println("Allow Credentials: " + allowCredentials);
            System.out.println("===================");
        }
    }
}


