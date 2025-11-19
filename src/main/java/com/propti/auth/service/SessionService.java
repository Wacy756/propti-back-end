package com.propti.auth.service;

import com.propti.auth.entity.Session;
import com.propti.auth.entity.User;
import com.propti.auth.model.UserPrincipal;
import com.propti.auth.repository.SessionRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    private final SessionRepository sessionRepository;

    @Transactional
    public Session createSession(final User user) {
        final Instant now = Instant.now();
        final Session session = Session.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .role(user.getRole())
                .createdAt(now)
                .expiresAt(now.plus(SESSION_TTL))
                .build();
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Optional<UserPrincipal> resolvePrincipal(final String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }

        return sessionRepository.findByTokenAndExpiresAtAfter(token, Instant.now())
                .map(session -> new UserPrincipal(session.getUserId(), session.getRole()));
    }

    @Transactional
    public void evictExpiredSessions() {
        sessionRepository.deleteByExpiresAtBefore(Instant.now());
    }

    public Duration sessionTtl() {
        return SESSION_TTL;
    }
}
