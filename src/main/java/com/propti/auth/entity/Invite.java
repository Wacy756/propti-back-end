package com.propti.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "invites")
public class Invite {

    public enum Status {
        PENDING,
        SENT,
        ACCEPTED,
        DECLINED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_email", nullable = false)
    private String tenantEmail;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "tenancy_id")
    private UUID tenancyId;

    @Column(name = "inviter_role")
    private String inviterRole;

    @Column(name = "inviter_name")
    private String inviterName;

    @Column(name = "property_address")
    private String propertyAddress;

    @Column(name = "invitee_role")
    private String inviteeRole = "tenant";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
