package com.propti.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record MaintenanceRequestDto(
        UUID id,
        String title,
        String description,
        String status,
        String propertyId,
        Instant createdAt,
        Instant updatedAt
) {}
