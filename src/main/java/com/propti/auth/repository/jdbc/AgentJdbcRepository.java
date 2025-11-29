package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.Agent;
import com.propti.auth.entity.Agent.AgentStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgentJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<Agent> ROW_MAPPER = AgentJdbcRepository::mapRow;

    public Optional<Agent> findById(String id) {
        String sql = """
                SELECT "ID","STATUS","CREATED_AT","UPDATED_AT"
                FROM "AGENTS"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public Agent save(Agent agent) {
        if (agent.getId() == null) {
            throw new IllegalArgumentException("Agent id is required");
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", agent.getId())
                .addValue("status", agent.getStatus() != null ? agent.getStatus().name() : AgentStatus.PENDING.name())
                .addValue("createdAt", toTimestamp(agent.getCreatedAt()))
                .addValue("updatedAt", toTimestamp(agent.getUpdatedAt()));

        String sql = """
                INSERT INTO "AGENTS" ("ID","STATUS","CREATED_AT","UPDATED_AT")
                VALUES (:id,:status,COALESCE(:createdAt, NOW()), COALESCE(:updatedAt, NOW()))
                ON CONFLICT ("ID")
                DO UPDATE SET "STATUS" = EXCLUDED."STATUS", "UPDATED_AT" = NOW()
                RETURNING "ID","STATUS","CREATED_AT","UPDATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    private static Agent mapRow(ResultSet rs, int rowNum) throws SQLException {
        Agent agent = new Agent();
        agent.setId(rs.getString("ID"));
        agent.setStatus(AgentStatus.valueOf(rs.getString("STATUS")));
        Timestamp created = rs.getTimestamp("CREATED_AT");
        Timestamp updated = rs.getTimestamp("UPDATED_AT");
        agent.setCreatedAt(created != null ? created.toInstant() : Instant.now());
        agent.setUpdatedAt(updated != null ? updated.toInstant() : Instant.now());
        return agent;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }
}
