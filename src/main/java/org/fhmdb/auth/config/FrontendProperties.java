package org.fhmdb.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Getter
@Component
@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {

    private String url;

    @Value("${debug.enabled:false}")
    private boolean debug;

    public void setUrl(String url) {
        this.url = url;
    }

    @PostConstruct
    public void logFrontendUrl() {
        if (debug) {
            System.out.println("Frontend redirect URL: " + url);
        }
    }
}
