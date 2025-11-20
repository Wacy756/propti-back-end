package com.propti.auth.repository;

import com.propti.auth.entity.MaintenanceRequest;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, java.util.UUID> {

    List<MaintenanceRequest> findByLandlordIdAndStatusIn(String landlordId, Collection<String> statuses);
}
