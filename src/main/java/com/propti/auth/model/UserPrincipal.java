package com.propti.auth.model;

import java.util.UUID;

public record UserPrincipal(UUID userId, String role) {
    public static final String REQUEST_ATTRIBUTE = "principal";
}
