package com.propti.auth.controller;

import com.propti.auth.dto.CreateDocumentRequest;
import com.propti.auth.dto.DocumentDto;
import com.propti.auth.service.DocumentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenancies/{tenancyId}/documents")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentDto> create(@PathVariable UUID tenancyId,
                                              @RequestBody CreateDocumentRequest request) {
        return ResponseEntity.ok(documentService.create(tenancyId, request));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> list(@PathVariable UUID tenancyId) {
        return ResponseEntity.ok(documentService.list(tenancyId));
    }
}
