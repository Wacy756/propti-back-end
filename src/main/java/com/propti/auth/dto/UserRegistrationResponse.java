package com.propti.auth.dto;

import java.util.UUID;

public record UserRegistrationResponse(UUID idm, String fullName, String email, String role) { }