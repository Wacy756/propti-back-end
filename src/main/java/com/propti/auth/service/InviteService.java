package com.propti.auth.service;

import com.propti.auth.dto.InviteDto;
import com.propti.auth.dto.InviteRequest;
import com.propti.auth.entity.Invite;
import com.propti.auth.entity.Invite.Status;
import com.propti.auth.entity.Tenancy.TenantStatus;
import com.propti.auth.entity.User;
import com.propti.auth.repository.jdbc.InviteJdbcRepository;
import com.propti.auth.repository.jdbc.TenancyJdbcRepository;
import com.propti.auth.repository.jdbc.UserJdbcRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final UserJdbcRepository userRepository;
    private final InviteJdbcRepository inviteRepository;
    private final TenancyJdbcRepository tenancyRepository;

    @Transactional
    public InviteDto sendTenantInvite(InviteRequest request) {
        Invite invite = new Invite();
        invite.setTenantEmail(request.tenantEmail());
        userRepository.findByEmailIgnoreCase(request.tenantEmail())
                .ifPresent(u -> invite.setTenantId(u.getId().toString()));
        invite.setTenancyId(request.tenancyId());
        invite.setInviterRole(request.inviterRole());
        invite.setInviteeRole(request.inviteeRole() != null ? request.inviteeRole() : "tenant");
        invite.setInviterName(request.inviterName());
        invite.setPropertyAddress(request.propertyAddress());
        invite.setStatus(Status.SENT);
        Invite saved = inviteRepository.save(invite);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<InviteDto> listForTenant(String tenantId) {
        return inviteRepository.findByTenantId(tenantId).stream().map(this::toDto).toList();
    }

    @Transactional
    public InviteDto accept(UUID inviteId) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Invite not found: " + inviteId));
        invite.setStatus(Status.ACCEPTED);
        inviteRepository.save(invite);

        if (invite.getTenancyId() != null) {
            tenancyRepository.findById(invite.getTenancyId()).ifPresent(tenancy -> {
                tenancy.setTenantStatus(TenantStatus.ACTIVE);
                tenancy.setInviteAcceptedAt(java.time.Instant.now());
                tenancy.setReferenceStatus(com.propti.auth.entity.Tenancy.ReferenceStatus.IN_PROGRESS);
                tenancyRepository.save(tenancy);
            });
        }
        return toDto(invite);
    }

    @Transactional
    public InviteDto decline(UUID inviteId) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Invite not found: " + inviteId));
        invite.setStatus(Status.DECLINED);
        inviteRepository.save(invite);
        if (invite.getTenancyId() != null) {
            tenancyRepository.findById(invite.getTenancyId()).ifPresent(tenancy -> {
                tenancy.setTenantStatus(TenantStatus.PENDING);
                tenancyRepository.save(tenancy);
            });
        }
        return toDto(invite);
    }

    private InviteDto toDto(Invite invite) {
        return new InviteDto(
                invite.getId(),
                invite.getTenantEmail(),
                invite.getTenantId(),
                invite.getTenancyId(),
                invite.getInviterRole(),
                invite.getInviteeRole(),
                invite.getInviterName(),
                invite.getPropertyAddress(),
                invite.getStatus(),
                invite.getCreatedAt()
        );
    }
}
