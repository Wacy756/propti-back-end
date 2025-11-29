package com.propti.auth.controller;

import com.propti.auth.dto.AgentStatusDto;
import com.propti.auth.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentStatusDto> getStatus(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.getStatus(agentId));
    }

    @PostMapping("/{agentId}/verify")
    public ResponseEntity<AgentStatusDto> verify(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.verifyAgent(agentId));
    }
}
