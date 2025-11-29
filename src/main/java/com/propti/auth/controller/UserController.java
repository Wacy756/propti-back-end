package com.propti.auth.controller;

import com.propti.auth.dto.NameUpdateRequest;
import com.propti.auth.dto.ProfileUpdateRequest;
import com.propti.auth.dto.RoleUpdateRequest;
import com.propti.auth.dto.UserDto;
import com.propti.auth.model.UserPrincipal;
import com.propti.auth.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDto> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/me/name")
    public ResponseEntity<UserDto> updateMyName(
            @RequestAttribute(name = UserPrincipal.REQUEST_ATTRIBUTE, required = false) UserPrincipal principal,
            @RequestBody NameUpdateRequest request
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (principal.email() == null || principal.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No email available to update name.");
        }
        UserDto updated = userService.upsertName(principal.email(), principal.role(), request.getName());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/role")
    public ResponseEntity<UserDto> updateMyRole(
            @RequestAttribute(name = UserPrincipal.REQUEST_ATTRIBUTE, required = false) UserPrincipal principal,
            @RequestBody RoleUpdateRequest request
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (principal.email() == null || principal.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No email available to update role.");
        }
        UserDto updated = userService.upsertRole(principal.email(), request.getRole(), principal.name());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserDto> updateProfile(
            @RequestAttribute(name = UserPrincipal.REQUEST_ATTRIBUTE, required = false) UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String email = request.getEmail() != null ? request.getEmail() : principal.email();
        UserDto updated = userService.upsertProfile(
                email,
                principal.role(),
                request.getName(),
                request.getPhone(),
                request.getCompanyName()
        );
        return ResponseEntity.ok(updated);
    }
}
