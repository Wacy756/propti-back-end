package com.propti.auth.dto;

import java.util.UUID;

public record AuthResponse(UUID userId, String role, String token) { }
