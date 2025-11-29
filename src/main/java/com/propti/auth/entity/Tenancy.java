package com.propti.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tenancies")
public class Tenancy {

    public enum TenantStatus {
        PENDING,
        ACTIVE
    }

    public enum ReferenceStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public enum AgreementStatus {
        NOT_SENT,
        SENT,
        SIGNED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "landlord_id", nullable = false)
    private String landlordId;

    @Column(name = "property_id", nullable = false)
    private String propertyId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "tenant_email")
    private String tenantEmail;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "monthly_rent")
    private Integer monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_status", nullable = false)
    private TenantStatus tenantStatus = TenantStatus.PENDING;

    @Column(name = "invite_sent_at")
    private Instant inviteSentAt;

    @Column(name = "invite_accepted_at")
    private Instant inviteAcceptedAt;

    @Column(name = "reference_status")
    @Enumerated(EnumType.STRING)
    private ReferenceStatus referenceStatus = ReferenceStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "agreement_status")
    private AgreementStatus agreementStatus = AgreementStatus.NOT_SENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (tenantStatus == null) {
            tenantStatus = TenantStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
