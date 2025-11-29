package com.propti.auth.repository;

import com.propti.auth.entity.Tenancy;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenancyRepository extends JpaRepository<Tenancy, UUID> {
    Optional<Tenancy> findFirstByTenantId(String tenantId);
    Optional<Tenancy> findFirstByPropertyId(String propertyId);
}
