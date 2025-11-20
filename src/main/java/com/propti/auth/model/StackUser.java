package com.propti.auth.model;

import java.util.Map;
import org.springframework.util.StringUtils;

public record StackUser(String id, Map<String, Object> clientMetadata, Map<String, Object> serverMetadata) {

    public String role() {
        final String fromServer = extractRole(serverMetadata);
        if (StringUtils.hasText(fromServer)) {
            return fromServer;
        }
        return extractRole(clientMetadata);
    }

    private String extractRole(final Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        final Object value = metadata.get("role");
        if (value instanceof String str && StringUtils.hasText(str)) {
            return str;
        }
        return null;
    }
}
