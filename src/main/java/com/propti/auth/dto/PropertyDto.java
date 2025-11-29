package com.propti.auth.dto;

import com.propti.auth.entity.Tenancy.AgreementStatus;
import com.propti.auth.entity.Tenancy.ReferenceStatus;
import com.propti.auth.entity.Tenancy.TenantStatus;
import java.time.Instant;
import java.util.UUID;

public record PropertyDto(
        String id,
        String landlordId,
        String name,
        String address,
        String postcode,
        Integer rent,
        String status,
        UUID tenancyId,
        String tenantName,
        String tenantEmail,
        Boolean paid,
        TenantStatus tenantStatus,
        ReferenceStatus referenceStatus,
        AgreementStatus agreementStatus,
        Instant createdAt
) {}
