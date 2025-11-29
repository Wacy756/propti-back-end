package com.propti.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        UUID tenancyId,
        String name,
        String type,
        String url,
        String sharedWith,
        Instant createdAt
) {}
