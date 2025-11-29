package com.propti.auth.dto;

import com.propti.auth.entity.Invite.Status;
import java.time.Instant;
import java.util.UUID;

public record InviteDto(
        UUID id,
        String tenantEmail,
        String tenantId,
        UUID tenancyId,
        String inviterRole,
        String inviteeRole,
        String inviterName,
        String propertyAddress,
        Status status,
        Instant createdAt
) {}
