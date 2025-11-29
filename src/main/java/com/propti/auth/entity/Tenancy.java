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
@Table(name = "TENANCIES")
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
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "LANDLORD_ID", nullable = false)
    private String landlordId;

    @Column(name = "PROPERTY_ID", nullable = false)
    private String propertyId;

    @Column(name = "TENANT_ID", nullable = false)
    private String tenantId;

    @Column(name = "TENANT_NAME")
    private String tenantName;

    @Column(name = "TENANT_EMAIL")
    private String tenantEmail;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "OWNER_EMAIL")
    private String ownerEmail;

    @Column(name = "START_DATE")
    private String startDate;

    @Column(name = "MONTHLY_RENT")
    private Integer monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(name = "TENANT_STATUS", nullable = false)
    private TenantStatus tenantStatus = TenantStatus.PENDING;

    @Column(name = "INVITE_SENT_AT")
    private Instant inviteSentAt;

    @Column(name = "INVITE_ACCEPTED_AT")
    private Instant inviteAcceptedAt;

    @Column(name = "REFERENCE_STATUS")
    @Enumerated(EnumType.STRING)
    private ReferenceStatus referenceStatus = ReferenceStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "AGREEMENT_STATUS")
    private AgreementStatus agreementStatus = AgreementStatus.NOT_SENT;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
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
