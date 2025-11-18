package com.propti.auth.service;

import com.propti.auth.dto.UserRegistrationRequest;
import com.propti.auth.dto.UserRegistrationResponse;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserService {
    private final Map<String, UserRegistrationResponse> usersByEmail = new HashMap<>();

    public synchronized UserRegistrationResponse register(UserRegistrationRequest req) {
        validate(req);
        if (usersByEmail.containsKey(req.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        UserRegistrationResponse user = new UserRegistrationResponse(
                UUID.randomUUID(),
                req.fullName(),
                req.email().toLowerCase(),
                req.role()
        );
        usersByEmail.put(user.email(), user);
        return user;
    }

    private void validate(UserRegistrationRequest req) {
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
}
