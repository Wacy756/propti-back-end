package com.propti.auth.dto;

public record CreateTenancyRequest(
        String tenantId,
        String tenantName,
        String tenantEmail,
        String ownerId,
        String ownerEmail,
        String startDate,
        Integer monthlyRent
) {}
