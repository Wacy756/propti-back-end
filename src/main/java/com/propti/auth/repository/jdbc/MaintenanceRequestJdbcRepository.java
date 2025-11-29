package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.MaintenanceRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MaintenanceRequestJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<MaintenanceRequest> ROW_MAPPER = MaintenanceRequestJdbcRepository::mapRow;

    public List<MaintenanceRequest> findByLandlordIdAndStatusIn(String landlordId, Collection<String> statuses) {
        String sql = """
                SELECT "ID","LANDLORD_ID","PROPERTY_ID","TITLE","DESCRIPTION","STATUS","CREATED_AT","UPDATED_AT"
                FROM "MAINTENANCE_REQUESTS"
                WHERE "LANDLORD_ID" = :landlordId AND "STATUS" IN (:statuses)
                ORDER BY "CREATED_AT" DESC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("landlordId", landlordId)
                .addValue("statuses", statuses);
        return template.query(sql, params, ROW_MAPPER);
    }

    private static MaintenanceRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        MaintenanceRequest req = new MaintenanceRequest();
        req.setId((UUID) rs.getObject("ID"));
        req.setLandlordId(rs.getString("LANDLORD_ID"));
        req.setPropertyId(rs.getString("PROPERTY_ID"));
        req.setTitle(rs.getString("TITLE"));
        req.setDescription(rs.getString("DESCRIPTION"));
        req.setStatus(rs.getString("STATUS"));
        req.setCreatedAt(toInstant(rs.getTimestamp("CREATED_AT")));
        req.setUpdatedAt(toInstant(rs.getTimestamp("UPDATED_AT")));
        return req;
    }

    private static Instant toInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
