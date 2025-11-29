package com.propti.auth.controller;

import com.propti.auth.dto.CreateTenancyRequest;
import com.propti.auth.dto.TenancyDto;
import com.propti.auth.service.TenancyService;
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
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class TenancyController {

    private final TenancyService tenancyService;

    @PostMapping("/landlords/{landlordId}/properties/{propertyId}/tenancies")
    public ResponseEntity<TenancyDto> createTenancy(@PathVariable String landlordId,
                                                    @PathVariable String propertyId,
                                                    @RequestBody CreateTenancyRequest request) {
        return ResponseEntity.ok(tenancyService.createTenancy(landlordId, propertyId, request));
    }

    @PostMapping("/tenancies/{tenancyId}/activate")
    public ResponseEntity<TenancyDto> activate(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(tenancyService.activateTenancy(tenancyId));
    }

    @PostMapping("/tenancies/{tenancyId}/references/complete")
    public ResponseEntity<TenancyDto> completeReferences(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(tenancyService.completeReferences(tenancyId));
    }

    @PostMapping("/tenancies/{tenancyId}/agreement/send")
    public ResponseEntity<TenancyDto> sendAgreement(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(tenancyService.sendAgreement(tenancyId));
    }

    @PostMapping("/tenancies/{tenancyId}/agreement/sign")
    public ResponseEntity<TenancyDto> signAgreement(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(tenancyService.signAgreement(tenancyId));
    }

    @GetMapping("/tenancies/{tenancyId}")
    public ResponseEntity<TenancyDto> getTenancy(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(tenancyService.getTenancy(tenancyId));
    }

    @GetMapping("/tenants/{tenantId}/tenancy")
    public ResponseEntity<TenancyDto> getByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenancyService.getTenancyForTenant(tenantId));
    }
}
