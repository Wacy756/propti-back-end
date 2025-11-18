package com.propti.controller;

import com.propti.auth.dto.UserRegistrationRequest;
import com.propti.auth.dto.UserRegistrationResponse;
import com.propti.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody final UserRegistrationRequest request) {
        return ResponseEntity.status(201).body(userService.register(request));
    }
}
