package com.propti.auth.entity;

import com.propti.auth.dto.UserRegistrationRequest;
import lombok.Getter;

import java.time.Instant;

@Getter
public class User {
    // private UUID id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String role;
    private final Instant createdAt = Instant.now();


    public User(final UserRegistrationRequest request) {
        // TODO -- how to randomly generate ID?

        this.fullName = request.fullName();
        this.email = request.email();
        this.passwordHash = request.password();
        this.role = request.role();
    }

}
