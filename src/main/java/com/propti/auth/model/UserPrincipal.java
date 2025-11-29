package com.propti.auth.model;

public record UserPrincipal(String userId, String role, String email, String name) {
    public static final String REQUEST_ATTRIBUTE = "principal";
}
