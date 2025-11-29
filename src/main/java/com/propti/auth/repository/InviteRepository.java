package com.propti.auth.repository;

import com.propti.auth.entity.Invite;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, UUID> {
    List<Invite> findByTenantId(String tenantId);
}
