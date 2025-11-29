package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.Property;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PropertyJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<Property> ROW_MAPPER = PropertyJdbcRepository::mapRow;

    public List<Property> findByLandlordId(String landlordId) {
        String sql = """
                SELECT "ID","LANDLORD_ID","NAME","ADDRESS","POSTCODE","RENT","STATUS","PAID","CREATED_AT"
                FROM "PROPERTIES"
                WHERE "LANDLORD_ID" = :landlordId
                ORDER BY "CREATED_AT" DESC
                """;
        return template.query(sql, new MapSqlParameterSource("landlordId", landlordId), ROW_MAPPER);
    }

    public Optional<Property> findById(String id) {
        String sql = """
                SELECT "ID","LANDLORD_ID","NAME","ADDRESS","POSTCODE","RENT","STATUS","PAID","CREATED_AT"
                FROM "PROPERTIES"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public Property save(Property property) {
        if (property.getCreatedAt() == null) {
            property.setCreatedAt(Instant.now());
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", property.getId())
                .addValue("landlordId", property.getLandlordId())
                .addValue("name", property.getName())
                .addValue("address", property.getAddress())
                .addValue("postcode", property.getPostcode())
                .addValue("rent", property.getRent())
                .addValue("status", property.getStatus())
                .addValue("paid", property.getPaid())
                .addValue("createdAt", toTimestamp(property.getCreatedAt()));

        String sql = """
                INSERT INTO "PROPERTIES" ("ID","LANDLORD_ID","NAME","ADDRESS","POSTCODE","RENT","STATUS","PAID","CREATED_AT")
                VALUES (:id,:landlordId,:name,:address,:postcode,:rent,:status,:paid,COALESCE(:createdAt, NOW()))
                ON CONFLICT ("ID")
                DO UPDATE SET "LANDLORD_ID" = EXCLUDED."LANDLORD_ID",
                              "NAME" = EXCLUDED."NAME",
                              "ADDRESS" = EXCLUDED."ADDRESS",
                              "POSTCODE" = EXCLUDED."POSTCODE",
                              "RENT" = EXCLUDED."RENT",
                              "STATUS" = EXCLUDED."STATUS",
                              "PAID" = EXCLUDED."PAID"
                RETURNING "ID","LANDLORD_ID","NAME","ADDRESS","POSTCODE","RENT","STATUS","PAID","CREATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    public void deleteById(String id) {
        template.update("DELETE FROM \"PROPERTIES\" WHERE \"ID\" = :id", new MapSqlParameterSource("id", id));
    }

    private static Property mapRow(ResultSet rs, int rowNum) throws SQLException {
        Property property = new Property();
        property.setId(rs.getString("ID"));
        property.setLandlordId(rs.getString("LANDLORD_ID"));
        property.setName(rs.getString("NAME"));
        property.setAddress(rs.getString("ADDRESS"));
        property.setPostcode(rs.getString("POSTCODE"));
        property.setRent((Integer) rs.getObject("RENT"));
        property.setStatus(rs.getString("STATUS"));
        property.setPaid((Boolean) rs.getObject("PAID"));
        property.setCreatedAt(toInstant(rs.getTimestamp("CREATED_AT")));
        return property;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static Instant toInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
