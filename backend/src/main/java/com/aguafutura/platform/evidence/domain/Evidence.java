package com.aguafutura.platform.evidence.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Evidence {

    private final UUID id;
    private final UUID tenantId;
    private final ReferenceType referenceType;
    private final UUID referenceId;
    private final EvidenceType evidenceType;
    
    private final String fileName;
    private final String contentType;
    private final String filePath;
    
    private final LocalDateTime createdAt;

    public Evidence(
            UUID id,
            UUID tenantId,
            ReferenceType referenceType,
            UUID referenceId,
            EvidenceType evidenceType,
            String fileName,
            String contentType,
            String filePath,
            LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (tenantId == null) throw new IllegalArgumentException("tenantId cannot be null");
        if (referenceType == null) throw new IllegalArgumentException("referenceType cannot be null");
        if (referenceId == null) throw new IllegalArgumentException("referenceId cannot be null");
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("fileName cannot be blank");
        if (filePath == null || filePath.isBlank()) throw new IllegalArgumentException("filePath cannot be blank");

        this.id = id;
        this.tenantId = tenantId;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.evidenceType = evidenceType != null ? evidenceType : EvidenceType.GENERAL_ATTACHMENT;
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Evidence create(
            UUID tenantId,
            ReferenceType referenceType,
            UUID referenceId,
            EvidenceType evidenceType,
            String fileName,
            String contentType,
            String filePath
    ) {
        return new Evidence(
                UUID.randomUUID(),
                tenantId,
                referenceType,
                referenceId,
                evidenceType,
                fileName,
                contentType,
                filePath,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public ReferenceType getReferenceType() { return referenceType; }
    public UUID getReferenceId() { return referenceId; }
    public EvidenceType getEvidenceType() { return evidenceType; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
