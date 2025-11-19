package com.propti.auth.service;

import com.propti.auth.dto.AuthResponse;
import com.propti.auth.dto.LoginRequest;
import com.propti.auth.dto.UserRegistrationRequest;
import com.propti.auth.entity.User;
import com.propti.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionService sessionService;

    @Transactional
    public AuthResponse register(final UserRegistrationRequest req) {
        validateRegistration(req);
        final String normalizedEmail = req.email().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        final User savedUser = userRepository.save(new User(new UserRegistrationRequest(
                req.fullName(),
                normalizedEmail,
                req.password(),
                req.role()
        )));

        return issueAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(final LoginRequest req) {
        validateLogin(req);
        final String normalizedEmail = req.email().toLowerCase();

        final User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!user.getPasswordHash().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return issueAuthResponse(user);
    }

    private void validateRegistration(final UserRegistrationRequest req) {
        if (!StringUtils.hasText(req.fullName())
                || !StringUtils.hasText(req.email())
                || !StringUtils.hasText(req.password())
                || !StringUtils.hasText(req.role())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields required");
        }
        if (req.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }
    }

    private void validateLogin(final LoginRequest req) {
        if (!StringUtils.hasText(req.email()) || !StringUtils.hasText(req.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password required");
        }
    }

    private AuthResponse issueAuthResponse(final User user) {
        sessionService.evictExpiredSessions();
        final var session = sessionService.createSession(user);
        return new AuthResponse(user.getId(), user.getRole(), session.getToken());
    }
}
