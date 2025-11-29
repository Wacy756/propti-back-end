package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.Invite;
import com.propti.auth.entity.Invite.Status;
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
public class InviteJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<Invite> ROW_MAPPER = InviteJdbcRepository::mapRow;

    public Invite save(Invite invite) {
        if (invite.getId() == null) {
            invite.setId(UUID.randomUUID());
            invite.setCreatedAt(Instant.now());
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", invite.getId())
                .addValue("tenantEmail", invite.getTenantEmail())
                .addValue("tenantId", invite.getTenantId())
                .addValue("tenancyId", invite.getTenancyId())
                .addValue("inviterRole", invite.getInviterRole())
                .addValue("inviterName", invite.getInviterName())
                .addValue("propertyAddress", invite.getPropertyAddress())
                .addValue("inviteeRole", invite.getInviteeRole())
                .addValue("status", invite.getStatus() != null ? invite.getStatus().name() : Status.PENDING.name())
                .addValue("createdAt", toTimestamp(invite.getCreatedAt()));

        String sql = """
                INSERT INTO "INVITES" ("ID","TENANT_EMAIL","TENANT_ID","TENANCY_ID","INVITER_ROLE","INVITER_NAME","PROPERTY_ADDRESS","INVITEE_ROLE","STATUS","CREATED_AT")
                VALUES (:id,:tenantEmail,:tenantId,:tenancyId,:inviterRole,:inviterName,:propertyAddress,:inviteeRole,:status,COALESCE(:createdAt, NOW()))
                ON CONFLICT ("ID")
                DO UPDATE SET "TENANT_EMAIL" = EXCLUDED."TENANT_EMAIL",
                              "TENANT_ID" = EXCLUDED."TENANT_ID",
                              "TENANCY_ID" = EXCLUDED."TENANCY_ID",
                              "INVITER_ROLE" = EXCLUDED."INVITER_ROLE",
                              "INVITER_NAME" = EXCLUDED."INVITER_NAME",
                              "PROPERTY_ADDRESS" = EXCLUDED."PROPERTY_ADDRESS",
                              "INVITEE_ROLE" = EXCLUDED."INVITEE_ROLE",
                              "STATUS" = EXCLUDED."STATUS"
                RETURNING "ID","TENANT_EMAIL","TENANT_ID","TENANCY_ID","INVITER_ROLE","INVITER_NAME","PROPERTY_ADDRESS","INVITEE_ROLE","STATUS","CREATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    public Optional<Invite> findById(UUID id) {
        String sql = """
                SELECT "ID","TENANT_EMAIL","TENANT_ID","TENANCY_ID","INVITER_ROLE","INVITER_NAME","PROPERTY_ADDRESS","INVITEE_ROLE","STATUS","CREATED_AT"
                FROM "INVITES"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public List<Invite> findByTenantId(String tenantId) {
        String sql = """
                SELECT "ID","TENANT_EMAIL","TENANT_ID","TENANCY_ID","INVITER_ROLE","INVITER_NAME","PROPERTY_ADDRESS","INVITEE_ROLE","STATUS","CREATED_AT"
                FROM "INVITES"
                WHERE "TENANT_ID" = :tenantId
                ORDER BY "CREATED_AT" DESC
                """;
        return template.query(sql, new MapSqlParameterSource("tenantId", tenantId), ROW_MAPPER);
    }

    private static Invite mapRow(ResultSet rs, int rowNum) throws SQLException {
        Invite invite = new Invite();
        invite.setId((UUID) rs.getObject("ID"));
        invite.setTenantEmail(rs.getString("TENANT_EMAIL"));
        invite.setTenantId(rs.getString("TENANT_ID"));
        invite.setTenancyId((UUID) rs.getObject("TENANCY_ID"));
        invite.setInviterRole(rs.getString("INVITER_ROLE"));
        invite.setInviterName(rs.getString("INVITER_NAME"));
        invite.setPropertyAddress(rs.getString("PROPERTY_ADDRESS"));
        invite.setInviteeRole(rs.getString("INVITEE_ROLE"));
        invite.setStatus(Status.valueOf(rs.getString("STATUS")));
        invite.setCreatedAt(toInstant(rs.getTimestamp("CREATED_AT")));
        return invite;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static Instant toInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
