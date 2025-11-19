package com.propti.auth.repository;

import com.propti.auth.entity.Session;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByTokenAndExpiresAtAfter(String token, Instant now);

    void deleteByExpiresAtBefore(Instant cutoff);
}
