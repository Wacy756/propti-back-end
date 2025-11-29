package com.propti.auth.service;

import com.propti.auth.dto.AgentStatusDto;
import com.propti.auth.entity.Agent;
import com.propti.auth.entity.Agent.AgentStatus;
import com.propti.auth.repository.jdbc.AgentJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentJdbcRepository agentRepository;

    @Transactional
    public AgentStatusDto verifyAgent(String agentId) {
        Agent agent = agentRepository.findById(agentId).orElseGet(() -> {
            Agent a = new Agent();
            a.setId(agentId);
            a.setStatus(AgentStatus.PENDING);
            return a;
        });
        agent.setStatus(AgentStatus.VERIFIED);
        Agent saved = agentRepository.save(agent);
        return new AgentStatusDto(saved.getId(), saved.getStatus(), saved.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public AgentStatusDto getStatus(String agentId) {
        Agent agent = agentRepository.findById(agentId).orElseGet(() -> {
            Agent a = new Agent();
            a.setId(agentId);
            a.setStatus(AgentStatus.PENDING);
            return a;
        });
        return new AgentStatusDto(agent.getId(), agent.getStatus(), agent.getUpdatedAt());
    }
}
