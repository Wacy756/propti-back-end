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
@Table(name = "INVITES")
public class Invite {

    public enum Status {
        PENDING,
        SENT,
        ACCEPTED,
        DECLINED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "TENANT_EMAIL", nullable = false)
    private String tenantEmail;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "TENANCY_ID")
    private UUID tenancyId;

    @Column(name = "INVITER_ROLE")
    private String inviterRole;

    @Column(name = "INVITER_NAME")
    private String inviterName;

    @Column(name = "PROPERTY_ADDRESS")
    private String propertyAddress;

    @Column(name = "INVITEE_ROLE")
    private String inviteeRole = "tenant";

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
