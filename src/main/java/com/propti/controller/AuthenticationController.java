package com.propti.controller;

import com.propti.auth.dto.AuthResponse;
import com.propti.auth.dto.LoginRequest;
import com.propti.auth.dto.UserRegistrationRequest;
import com.propti.auth.service.SessionService;
import com.propti.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody final UserRegistrationRequest request) {
        final AuthResponse authResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, buildSessionCookie(authResponse.token()).toString())
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {
        final AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildSessionCookie(authResponse.token()).toString())
                .body(authResponse);
    }

    private ResponseCookie buildSessionCookie(final String token) {
        return ResponseCookie.from("SESSION", token)
                .httpOnly(true)
                // For cross-origin local dev, SameSite=None is required. Set secure(false) for HTTP localhost;
                // switch to true in HTTPS environments.
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(sessionService.sessionTtl())
                .build();
    }
}
