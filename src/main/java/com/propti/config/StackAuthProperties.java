package com.propti.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stack")
public class StackAuthProperties {

    private String apiUrl = "https://api.stack-auth.com";
    private String projectId;
    private String secretServerKey;

    @PostConstruct
    void validate() {
        Assert.hasText(projectId, "stack.project-id must be set");
        Assert.hasText(secretServerKey, "stack.secret-server-key must be set");
    }

    public String apiUrl() {
        return StringUtils.hasText(apiUrl) ? apiUrl : "https://api.stack-auth.com";
    }
}
