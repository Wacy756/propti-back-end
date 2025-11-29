package com.propti.auth.service;

import com.propti.auth.dto.CreateTenancyRequest;
import com.propti.auth.dto.TenancyDto;
import com.propti.auth.entity.Tenancy;
import com.propti.auth.entity.Tenancy.AgreementStatus;
import com.propti.auth.entity.Tenancy.ReferenceStatus;
import com.propti.auth.entity.Tenancy.TenantStatus;
import com.propti.auth.repository.jdbc.PropertyJdbcRepository;
import com.propti.auth.repository.jdbc.TenancyJdbcRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenancyService {

    private final TenancyJdbcRepository tenancyRepository;
    private final DocumentService documentService;
    private final PropertyJdbcRepository propertyRepository;

    @Transactional
    public TenancyDto createTenancy(String landlordId, String propertyId, CreateTenancyRequest request) {
        Tenancy tenancy = new Tenancy();
        tenancy.setLandlordId(landlordId);
        tenancy.setPropertyId(propertyId);
        tenancy.setTenantId(request.tenantId());
        tenancy.setTenantName(request.tenantName());
        tenancy.setTenantEmail(request.tenantEmail());
        tenancy.setOwnerId(request.ownerId());
        tenancy.setOwnerEmail(request.ownerEmail());
        tenancy.setStartDate(request.startDate());
        tenancy.setMonthlyRent(request.monthlyRent());
        tenancy.setTenantStatus(TenantStatus.PENDING);
        tenancy.setInviteSentAt(Instant.now());
        tenancy.setReferenceStatus(ReferenceStatus.NOT_STARTED);
        tenancy.setAgreementStatus(AgreementStatus.NOT_SENT);
        Tenancy saved = tenancyRepository.save(tenancy);
        propertyRepository.findById(propertyId).ifPresent(prop -> {
            prop.setStatus("Occupied");
            propertyRepository.save(prop);
        });
        return toDto(saved);
    }

    @Transactional
    public TenancyDto activateTenancy(UUID tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found: " + tenancyId));
        tenancy.setTenantStatus(TenantStatus.ACTIVE);
        tenancy.setInviteAcceptedAt(Instant.now());
        tenancy.setReferenceStatus(ReferenceStatus.IN_PROGRESS);
        return toDto(tenancyRepository.save(tenancy));
    }

    @Transactional
    public TenancyDto completeReferences(UUID tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found: " + tenancyId));
        tenancy.setReferenceStatus(ReferenceStatus.COMPLETED);
        return toDto(tenancyRepository.save(tenancy));
    }

    @Transactional
    public TenancyDto sendAgreement(UUID tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found: " + tenancyId));
        tenancy.setAgreementStatus(AgreementStatus.SENT);
        return toDto(tenancyRepository.save(tenancy));
    }

    @Transactional
    public TenancyDto signAgreement(UUID tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found: " + tenancyId));
        tenancy.setAgreementStatus(AgreementStatus.SIGNED);
        Tenancy saved = tenancyRepository.save(tenancy);

        // Create a simple document entry for the signed agreement (URL mocked)
        documentService.create(
                tenancyId,
                new com.propti.auth.dto.CreateDocumentRequest(
                        "Signed tenancy agreement",
                        "AGREEMENT",
                        "/documents/tenancy-agreement.pdf",
                        "ALL"
                )
        );

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public TenancyDto getTenancy(UUID tenancyId) {
        return tenancyRepository.findById(tenancyId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found: " + tenancyId));
    }

    @Transactional(readOnly = true)
    public TenancyDto getTenancyForTenant(String tenantId) {
        return tenancyRepository.findFirstByTenantId(tenantId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Tenancy not found for tenant: " + tenantId));
    }

    private TenancyDto toDto(Tenancy tenancy) {
        return new TenancyDto(
                tenancy.getId(),
                tenancy.getLandlordId(),
                tenancy.getPropertyId(),
                tenancy.getTenantId(),
                tenancy.getTenantName(),
                tenancy.getTenantEmail(),
                tenancy.getOwnerId(),
                tenancy.getOwnerEmail(),
                tenancy.getStartDate(),
                tenancy.getMonthlyRent(),
                tenancy.getTenantStatus(),
                tenancy.getInviteSentAt(),
                tenancy.getInviteAcceptedAt(),
                tenancy.getReferenceStatus(),
                tenancy.getAgreementStatus(),
                tenancy.getCreatedAt(),
                tenancy.getUpdatedAt()
        );
    }
}
