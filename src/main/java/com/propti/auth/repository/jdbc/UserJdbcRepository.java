package com.propti.auth.repository.jdbc;

import com.propti.auth.entity.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final NamedParameterJdbcTemplate template;

    private static final RowMapper<User> ROW_MAPPER = UserJdbcRepository::mapRow;

    public Optional<User> findByEmailIgnoreCase(String email) {
        String sql = """
                SELECT "ID","EMAIL","ROLE","NAME","PHONE","COMPANY_NAME","CREATED_AT"
                FROM "USERS"
                WHERE lower("EMAIL") = lower(:email)
                """;
        return template.query(sql, new MapSqlParameterSource("email", email), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public Optional<User> findById(UUID id) {
        String sql = """
                SELECT "ID","EMAIL","ROLE","NAME","PHONE","COMPANY_NAME","CREATED_AT"
                FROM "USERS"
                WHERE "ID" = :id
                """;
        return template.query(sql, new MapSqlParameterSource("id", id), rs -> rs.next() ? Optional.of(mapRow(rs, 0)) : Optional.empty());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            user.setCreatedAt(Instant.now());
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("role", user.getRole())
                .addValue("name", user.getName())
                .addValue("phone", user.getPhone())
                .addValue("companyName", user.getCompanyName())
                .addValue("createdAt", toTimestamp(user.getCreatedAt()));

        String sql = """
                INSERT INTO "USERS" ("ID","EMAIL","ROLE","NAME","PHONE","COMPANY_NAME","CREATED_AT")
                VALUES (:id,:email,:role,:name,:phone,:companyName,COALESCE(:createdAt, NOW()))
                ON CONFLICT ("ID") DO UPDATE SET
                    "EMAIL" = EXCLUDED."EMAIL",
                    "ROLE" = EXCLUDED."ROLE",
                    "NAME" = EXCLUDED."NAME",
                    "PHONE" = EXCLUDED."PHONE",
                    "COMPANY_NAME" = EXCLUDED."COMPANY_NAME"
                RETURNING "ID","EMAIL","ROLE","NAME","PHONE","COMPANY_NAME","CREATED_AT"
                """;
        return template.queryForObject(sql, params, ROW_MAPPER);
    }

    private static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId((UUID) rs.getObject("ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setRole(rs.getString("ROLE"));
        user.setName(rs.getString("NAME"));
        user.setPhone(rs.getString("PHONE"));
        user.setCompanyName(rs.getString("COMPANY_NAME"));
        user.setCreatedAt(toInstant(rs.getTimestamp("CREATED_AT")));
        return user;
    }

    private static Instant toInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }

    private static Timestamp toTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }
}
