package com.propti.auth.controller;

import com.propti.auth.dto.InviteDto;
import com.propti.auth.dto.InviteRequest;
import com.propti.auth.service.InviteService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invites")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @PostMapping("/tenant")
    public ResponseEntity<InviteDto> sendInvite(@RequestBody InviteRequest request) {
        return ResponseEntity.ok(inviteService.sendTenantInvite(request));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<InviteDto>> listForTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(inviteService.listForTenant(tenantId));
    }

    @PostMapping("/{inviteId}/accept")
    public ResponseEntity<InviteDto> accept(@PathVariable UUID inviteId) {
        return ResponseEntity.ok(inviteService.accept(inviteId));
    }

    @PostMapping("/{inviteId}/decline")
    public ResponseEntity<InviteDto> decline(@PathVariable UUID inviteId) {
        return ResponseEntity.ok(inviteService.decline(inviteId));
    }
}
