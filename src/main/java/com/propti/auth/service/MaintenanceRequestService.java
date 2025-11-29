package com.propti.auth.service;

import com.propti.auth.dto.MaintenanceRequestDto;
import com.propti.auth.entity.MaintenanceRequest;
import com.propti.auth.repository.jdbc.MaintenanceRequestJdbcRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private static final List<String> OPEN_STATUSES = List.of("OPEN", "IN_PROGRESS");

    private final MaintenanceRequestJdbcRepository maintenanceRequestRepository;

    @Transactional(readOnly = true)
    public List<MaintenanceRequestDto> getOpenRequestsForLandlord(final String landlordId) {
        return maintenanceRequestRepository.findByLandlordIdAndStatusIn(landlordId, OPEN_STATUSES)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private MaintenanceRequestDto toDto(MaintenanceRequest request) {
        return new MaintenanceRequestDto(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPropertyId(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
