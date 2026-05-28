package com.aguafutura.platform.evidence.application;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.application.port.EvidenceStoragePort;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.EvidenceType;
import com.aguafutura.platform.evidence.domain.ReferenceType;

import java.io.InputStream;
import java.util.UUID;

public class UploadEvidenceUseCase {

    private final EvidenceRepositoryPort repository;
    private final EvidenceStoragePort storage;
    private final AuditLogPort auditLogPort;

    public UploadEvidenceUseCase(EvidenceRepositoryPort repository, EvidenceStoragePort storage, AuditLogPort auditLogPort) {
        this.repository = repository;
        this.storage = storage;
        this.auditLogPort = auditLogPort;
    }

    public Evidence execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            ReferenceType referenceType,
            UUID referenceId,
            EvidenceType evidenceType,
            String fileName,
            String contentType,
            InputStream content
    ) {
        // 1. Save file to storage
        String filePath = storage.saveFile(tenantId, fileName, content);

        // 2. Save metadata to DB
        Evidence evidence = Evidence.create(
                tenantId,
                referenceType,
                referenceId,
                evidenceType,
                fileName,
                contentType,
                filePath
        );

        Evidence savedEvidence = repository.save(evidence);

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "EVIDENCE_UPLOADED",
                "EVIDENCE",
                savedEvidence.getId().toString(),
                correlationId
        ));

        return savedEvidence;
    }
}
