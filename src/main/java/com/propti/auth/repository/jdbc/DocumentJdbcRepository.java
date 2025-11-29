package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.Document;
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
public class DocumentJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<Document> ROW_MAPPER = DocumentJdbcRepository::mapRow;

    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID());
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", document.getId())
                .addValue("tenancyId", document.getTenancyId())
                .addValue("name", document.getName())
                .addValue("type", document.getType())
                .addValue("url", document.getUrl())
                .addValue("sharedWith", document.getSharedWith())
                .addValue("createdAt", toTimestamp(document.getCreatedAt()));

        String sql = """
                INSERT INTO "DOCUMENTS" ("ID","TENANCY_ID","NAME","TYPE","URL","SHARED_WITH","CREATED_AT")
                VALUES (:id,:tenancyId,:name,:type,:url,:sharedWith,COALESCE(:createdAt, NOW()))
                ON CONFLICT ("ID")
                DO UPDATE SET "NAME" = EXCLUDED."NAME",
                              "TYPE" = EXCLUDED."TYPE",
                              "URL" = EXCLUDED."URL",
                              "SHARED_WITH" = EXCLUDED."SHARED_WITH"
                RETURNING "ID","TENANCY_ID","NAME","TYPE","URL","SHARED_WITH","CREATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    public List<Document> findByTenancyId(UUID tenancyId) {
        String sql = """
                SELECT "ID","TENANCY_ID","NAME","TYPE","URL","SHARED_WITH","CREATED_AT"
                FROM "DOCUMENTS"
                WHERE "TENANCY_ID" = :tenancyId
                ORDER BY "CREATED_AT" DESC
                """;
        return template.query(sql, new MapSqlParameterSource("tenancyId", tenancyId), ROW_MAPPER);
    }

    public Optional<Document> findById(UUID id) {
        String sql = """
                SELECT "ID","TENANCY_ID","NAME","TYPE","URL","SHARED_WITH","CREATED_AT"
                FROM "DOCUMENTS"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    private static Document mapRow(ResultSet rs, int rowNum) throws SQLException {
        Document doc = new Document();
        doc.setId((UUID) rs.getObject("ID"));
        doc.setTenancyId((UUID) rs.getObject("TENANCY_ID"));
        doc.setName(rs.getString("NAME"));
        doc.setType(rs.getString("TYPE"));
        doc.setUrl(rs.getString("URL"));
        doc.setSharedWith(rs.getString("SHARED_WITH"));
        Timestamp created = rs.getTimestamp("CREATED_AT");
        doc.setCreatedAt(created != null ? created.toInstant() : Instant.now());
        return doc;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }
}
