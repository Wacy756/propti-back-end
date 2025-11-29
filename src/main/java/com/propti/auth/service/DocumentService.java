package com.propti.auth.service;

import com.propti.auth.dto.CreateDocumentRequest;
import com.propti.auth.dto.DocumentDto;
import com.propti.auth.entity.Document;
import com.propti.auth.repository.DocumentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentDto create(UUID tenancyId, CreateDocumentRequest request) {
        Document doc = new Document();
        doc.setTenancyId(tenancyId);
        doc.setName(request.name());
        doc.setType(request.type());
        doc.setUrl(request.url());
        doc.setSharedWith(request.sharedWith());
        Document saved = documentRepository.save(doc);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> list(UUID tenancyId) {
        return documentRepository.findByTenancyId(tenancyId).stream().map(this::toDto).toList();
    }

    private DocumentDto toDto(Document doc) {
        return new DocumentDto(
                doc.getId(),
                doc.getTenancyId(),
                doc.getName(),
                doc.getType(),
                doc.getUrl(),
                doc.getSharedWith(),
                doc.getCreatedAt()
        );
    }
}
