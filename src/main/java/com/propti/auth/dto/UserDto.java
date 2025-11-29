package com.propti.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String role,
        String name,
        String phone,
        String companyName,
        Instant createdAt
) {}
