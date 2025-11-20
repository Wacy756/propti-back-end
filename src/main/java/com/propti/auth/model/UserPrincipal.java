package com.propti.auth.model;

public record UserPrincipal(String userId, String role) {
    public static final String REQUEST_ATTRIBUTE = "principal";
}
