package com.propti.auth.controller;

import com.propti.auth.dto.CurrentUserDto;
import com.propti.auth.model.UserPrincipal;
import com.propti.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
public class MeController {

    private final UserService userService;

    public MeController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> me(HttpServletRequest request) {
        final Object attribute = request.getAttribute(UserPrincipal.REQUEST_ATTRIBUTE);
        if (attribute instanceof UserPrincipal principal) {
            final CurrentUserDto dto = new CurrentUserDto(
                    principal.userId(),
                    principal.role(),
                    principal.email(),
                    principal.name(),
                    null,
                    null
            );
            // Try UUID lookup
            try {
                final UUID id = UUID.fromString(principal.userId());
                userService.findOptional(id).ifPresent(user -> {
                    if (dto.getEmail() == null) {
                        dto.setEmail(user.getEmail());
                    }
                    if (dto.getName() == null) {
                        dto.setName(user.getName());
                    }
                    dto.setPhone(user.getPhone());
                    dto.setCompanyName(user.getCompanyName());
                    if (dto.getRole() == null && user.getRole() != null) {
                        dto.setRole(user.getRole());
                    }
                });
            } catch (IllegalArgumentException ignored) {
                // Stack user ids may not be UUIDs; continue to email lookup.
            }
            // Fallback: look up by email if present
            if (dto.getEmail() != null) {
                userService.findOptionalByEmail(dto.getEmail()).ifPresent(user -> {
                    if (dto.getName() == null) {
                        dto.setName(user.getName());
                    }
                    dto.setPhone(user.getPhone());
                    dto.setCompanyName(user.getCompanyName());
                    if (dto.getRole() == null && user.getRole() != null) {
                        dto.setRole(user.getRole());
                    }
                });
            }
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(401).build();
    }
}
