package org.fhmdb.auth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }
}

