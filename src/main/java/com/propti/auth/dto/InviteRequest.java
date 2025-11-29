package com.propti.auth.dto;

import java.util.UUID;

public record InviteRequest(
        String tenantEmail,
        String inviterRole,
        UUID tenancyId,
        String inviteeRole,
        String inviterName,
        String propertyAddress
) {}
