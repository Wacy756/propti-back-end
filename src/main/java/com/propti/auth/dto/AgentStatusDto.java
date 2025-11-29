package com.propti.auth.dto;

import com.propti.auth.entity.Agent.AgentStatus;
import java.time.Instant;

public record AgentStatusDto(
        String agentId,
        AgentStatus status,
        Instant updatedAt
) {}
