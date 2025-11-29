package com.propti.auth.dto;

public record CreateDocumentRequest(
        String name,
        String type,
        String url,
        String sharedWith
) {}
