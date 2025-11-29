package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.Tenancy;
import com.propti.auth.entity.Tenancy.AgreementStatus;
import com.propti.auth.entity.Tenancy.ReferenceStatus;
import com.propti.auth.entity.Tenancy.TenantStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TenancyJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<Tenancy> ROW_MAPPER = TenancyJdbcRepository::mapRow;

    public Tenancy save(Tenancy tenancy) {
        if (tenancy.getId() == null) {
            tenancy.setId(UUID.randomUUID());
            tenancy.setCreatedAt(Instant.now());
        }
        tenancy.setUpdatedAt(Instant.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", tenancy.getId())
                .addValue("landlordId", tenancy.getLandlordId())
                .addValue("propertyId", tenancy.getPropertyId())
                .addValue("tenantId", tenancy.getTenantId())
                .addValue("tenantName", tenancy.getTenantName())
                .addValue("tenantEmail", tenancy.getTenantEmail())
                .addValue("ownerId", tenancy.getOwnerId())
                .addValue("ownerEmail", tenancy.getOwnerEmail())
                .addValue("startDate", tenancy.getStartDate())
                .addValue("monthlyRent", tenancy.getMonthlyRent())
                .addValue("tenantStatus", tenancy.getTenantStatus() != null ? tenancy.getTenantStatus().name() : TenantStatus.PENDING.name())
                .addValue("inviteSentAt", toTimestamp(tenancy.getInviteSentAt()))
                .addValue("inviteAcceptedAt", toTimestamp(tenancy.getInviteAcceptedAt()))
                .addValue("referenceStatus", tenancy.getReferenceStatus() != null ? tenancy.getReferenceStatus().name() : ReferenceStatus.NOT_STARTED.name())
                .addValue("agreementStatus", tenancy.getAgreementStatus() != null ? tenancy.getAgreementStatus().name() : AgreementStatus.NOT_SENT.name())
                .addValue("createdAt", toTimestamp(tenancy.getCreatedAt()))
                .addValue("updatedAt", toTimestamp(tenancy.getUpdatedAt()));

        String sql = """
                INSERT INTO "TENANCIES" ("ID","LANDLORD_ID","PROPERTY_ID","TENANT_ID","TENANT_NAME","TENANT_EMAIL","OWNER_ID","OWNER_EMAIL",
                                         "START_DATE","MONTHLY_RENT","TENANT_STATUS","INVITE_SENT_AT","INVITE_ACCEPTED_AT","REFERENCE_STATUS",
                                         "AGREEMENT_STATUS","CREATED_AT","UPDATED_AT")
                VALUES (:id,:landlordId,:propertyId,:tenantId,:tenantName,:tenantEmail,:ownerId,:ownerEmail,
                        :startDate,:monthlyRent,:tenantStatus,:inviteSentAt,:inviteAcceptedAt,:referenceStatus,
                        :agreementStatus,COALESCE(:createdAt,NOW()),COALESCE(:updatedAt,NOW()))
                ON CONFLICT ("ID")
                DO UPDATE SET "LANDLORD_ID" = EXCLUDED."LANDLORD_ID",
                              "PROPERTY_ID" = EXCLUDED."PROPERTY_ID",
                              "TENANT_ID" = EXCLUDED."TENANT_ID",
                              "TENANT_NAME" = EXCLUDED."TENANT_NAME",
                              "TENANT_EMAIL" = EXCLUDED."TENANT_EMAIL",
                              "OWNER_ID" = EXCLUDED."OWNER_ID",
                              "OWNER_EMAIL" = EXCLUDED."OWNER_EMAIL",
                              "START_DATE" = EXCLUDED."START_DATE",
                              "MONTHLY_RENT" = EXCLUDED."MONTHLY_RENT",
                              "TENANT_STATUS" = EXCLUDED."TENANT_STATUS",
                              "INVITE_SENT_AT" = EXCLUDED."INVITE_SENT_AT",
                              "INVITE_ACCEPTED_AT" = EXCLUDED."INVITE_ACCEPTED_AT",
                              "REFERENCE_STATUS" = EXCLUDED."REFERENCE_STATUS",
                              "AGREEMENT_STATUS" = EXCLUDED."AGREEMENT_STATUS",
                              "UPDATED_AT" = NOW()
                RETURNING "ID","LANDLORD_ID","PROPERTY_ID","TENANT_ID","TENANT_NAME","TENANT_EMAIL","OWNER_ID","OWNER_EMAIL",
                          "START_DATE","MONTHLY_RENT","TENANT_STATUS","INVITE_SENT_AT","INVITE_ACCEPTED_AT","REFERENCE_STATUS",
                          "AGREEMENT_STATUS","CREATED_AT","UPDATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    public Optional<Tenancy> findById(UUID id) {
        String sql = """
                SELECT "ID","LANDLORD_ID","PROPERTY_ID","TENANT_ID","TENANT_NAME","TENANT_EMAIL","OWNER_ID","OWNER_EMAIL",
                       "START_DATE","MONTHLY_RENT","TENANT_STATUS","INVITE_SENT_AT","INVITE_ACCEPTED_AT","REFERENCE_STATUS",
                       "AGREEMENT_STATUS","CREATED_AT","UPDATED_AT"
                FROM "TENANCIES"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public Optional<Tenancy> findFirstByTenantId(String tenantId) {
        String sql = """
                SELECT "ID","LANDLORD_ID","PROPERTY_ID","TENANT_ID","TENANT_NAME","TENANT_EMAIL","OWNER_ID","OWNER_EMAIL",
                       "START_DATE","MONTHLY_RENT","TENANT_STATUS","INVITE_SENT_AT","INVITE_ACCEPTED_AT","REFERENCE_STATUS",
                       "AGREEMENT_STATUS","CREATED_AT","UPDATED_AT"
                FROM "TENANCIES"
                WHERE "TENANT_ID" = :tenantId
                ORDER BY "CREATED_AT" DESC
                LIMIT 1
                """;
        return template.query(sql, new MapSqlParameterSource("tenantId", tenantId), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public Optional<Tenancy> findFirstByPropertyId(String propertyId) {
        String sql = """
                SELECT "ID","LANDLORD_ID","PROPERTY_ID","TENANT_ID","TENANT_NAME","TENANT_EMAIL","OWNER_ID","OWNER_EMAIL",
                       "START_DATE","MONTHLY_RENT","TENANT_STATUS","INVITE_SENT_AT","INVITE_ACCEPTED_AT","REFERENCE_STATUS",
                       "AGREEMENT_STATUS","CREATED_AT","UPDATED_AT"
                FROM "TENANCIES"
                WHERE "PROPERTY_ID" = :propertyId
                ORDER BY "CREATED_AT" DESC
                LIMIT 1
                """;
        return template.query(sql, new MapSqlParameterSource("propertyId", propertyId), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public void delete(UUID id) {
        template.update("DELETE FROM \"TENANCIES\" WHERE \"ID\" = :id", new MapSqlParameterSource("id", id));
    }

    private static Tenancy mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tenancy tenancy = new Tenancy();
        tenancy.setId((UUID) rs.getObject("ID"));
        tenancy.setLandlordId(rs.getString("LANDLORD_ID"));
        tenancy.setPropertyId(rs.getString("PROPERTY_ID"));
        tenancy.setTenantId(rs.getString("TENANT_ID"));
        tenancy.setTenantName(rs.getString("TENANT_NAME"));
        tenancy.setTenantEmail(rs.getString("TENANT_EMAIL"));
        tenancy.setOwnerId(rs.getString("OWNER_ID"));
        tenancy.setOwnerEmail(rs.getString("OWNER_EMAIL"));
        tenancy.setStartDate(rs.getString("START_DATE"));
        tenancy.setMonthlyRent((Integer) rs.getObject("MONTHLY_RENT"));
        tenancy.setTenantStatus(TenantStatus.valueOf(rs.getString("TENANT_STATUS")));
        tenancy.setInviteSentAt(toInstant(rs.getTimestamp("INVITE_SENT_AT")));
        tenancy.setInviteAcceptedAt(toInstant(rs.getTimestamp("INVITE_ACCEPTED_AT")));
        tenancy.setReferenceStatus(ReferenceStatus.valueOf(rs.getString("REFERENCE_STATUS")));
        tenancy.setAgreementStatus(AgreementStatus.valueOf(rs.getString("AGREEMENT_STATUS")));
        tenancy.setCreatedAt(toInstant(rs.getTimestamp("CREATED_AT")));
        tenancy.setUpdatedAt(toInstant(rs.getTimestamp("UPDATED_AT")));
        return tenancy;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static Instant toInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
