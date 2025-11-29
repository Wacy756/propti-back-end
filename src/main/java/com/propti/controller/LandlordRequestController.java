package com.propti.controller;

import com.propti.auth.dto.MaintenanceRequestDto;
import com.propti.auth.model.UserPrincipal;
import com.propti.auth.service.MaintenanceRequestService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/landlord")
@RequiredArgsConstructor
public class LandlordRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping("/requests/open")
    public List<MaintenanceRequestDto> getOpenRequests(@RequestAttribute(name = UserPrincipal.REQUEST_ATTRIBUTE, required = false) UserPrincipal principal) {
        UserPrincipal authenticated = requireAuthenticated(principal);
        requireRole(authenticated, "landlord-plus");
        return maintenanceRequestService.getOpenRequestsForLandlord(authenticated.userId());
    }

    private UserPrincipal requireAuthenticated(UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return principal;
    }

    private void requireRole(UserPrincipal principal, String role) {
        if (principal == null || !role.equalsIgnoreCase(principal.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }
}
