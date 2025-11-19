package com.propti.auth.repository;

import com.propti.auth.entity.MaintenanceRequest;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {

    List<MaintenanceRequest> findByLandlordIdAndStatusIn(UUID landlordId, Collection<String> statuses);
}
