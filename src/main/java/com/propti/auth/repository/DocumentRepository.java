package com.propti.auth.repository;

import com.propti.auth.entity.Document;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByTenancyId(UUID tenancyId);
}
