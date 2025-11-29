package com.propti.auth.model;

import java.util.Map;
import org.springframework.util.StringUtils;

public record StackUser(
        String id,
        String email,
        String name,
        Map<String, Object> clientMetadata,
        Map<String, Object> serverMetadata
) {

    public String resolvedEmail() {
        if (StringUtils.hasText(email)) {
            return email;
        }
        final String fromServer = extractString(serverMetadata, "email");
        if (StringUtils.hasText(fromServer)) {
            return fromServer;
        }
        return extractString(clientMetadata, "email");
    }

    public String resolvedName() {
        if (StringUtils.hasText(name)) {
            return name;
        }
        final String fromServer = extractString(serverMetadata, "name");
        if (StringUtils.hasText(fromServer)) {
            return fromServer;
        }
        final String fromClient = extractString(clientMetadata, "name");
        if (StringUtils.hasText(fromClient)) {
            return fromClient;
        }
        // Some providers use full_name
        final String full = extractString(serverMetadata, "full_name");
        if (StringUtils.hasText(full)) {
            return full;
        }
        return extractString(clientMetadata, "full_name");
    }

    public String role() {
        final String fromServer = extractString(serverMetadata, "role");
        if (StringUtils.hasText(fromServer)) {
            return fromServer;
        }
        return extractString(clientMetadata, "role");
    }

    private String extractString(final Map<String, Object> metadata, final String key) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        final Object value = metadata.get(key);
        if (value instanceof String str && StringUtils.hasText(str)) {
            return str;
        }
        return null;
    }
}
