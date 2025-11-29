package com.propti.auth.dto;

import com.propti.auth.entity.Tenancy.TenantStatus;
import java.time.Instant;
import java.util.UUID;

public record TenancyDto(
        UUID id,
        String landlordId,
        String propertyId,
        String tenantId,
        String tenantName,
        String tenantEmail,
        String ownerId,
        String ownerEmail,
        String startDate,
        Integer monthlyRent,
        TenantStatus tenantStatus,
        Instant inviteSentAt,
        Instant inviteAcceptedAt,
        com.propti.auth.entity.Tenancy.ReferenceStatus referenceStatus,
        com.propti.auth.entity.Tenancy.AgreementStatus agreementStatus,
        Instant createdAt,
        Instant updatedAt
) {}
