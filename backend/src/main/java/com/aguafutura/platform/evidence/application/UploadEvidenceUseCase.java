package com.aguafutura.platform.evidence.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.evidence.application.port.EvidenceRepositoryPort;
import com.aguafutura.platform.evidence.application.port.EvidenceStoragePort;
import com.aguafutura.platform.evidence.domain.Evidence;
import com.aguafutura.platform.evidence.domain.ReferenceType;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;

import java.io.InputStream;
import java.util.UUID;

public class UploadEvidenceUseCase {

    private final EvidenceRepositoryPort repository;
    private final EvidenceStoragePort storage;
    private final AssetRepositoryPort assetRepositoryPort;
    private final IncidentRepositoryPort incidentRepositoryPort;
    private final WorkOrderRepositoryPort workOrderRepositoryPort;

    public UploadEvidenceUseCase(
            EvidenceRepositoryPort repository,
            EvidenceStoragePort storage,
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort,
            WorkOrderRepositoryPort workOrderRepositoryPort
    ) {
        this.repository = repository;
        this.storage = storage;
        this.assetRepositoryPort = assetRepositoryPort;
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.workOrderRepositoryPort = workOrderRepositoryPort;
    }

    public Evidence execute(
            UUID tenantId,
            ReferenceType referenceType,
            UUID referenceId,
            String fileName,
            String contentType,
            InputStream content
    ) {
        validateReferenceBelongsToTenant(tenantId, referenceType, referenceId);

        // 1. Save file to storage
        String filePath = storage.saveFile(tenantId, fileName, content);

        // 2. Save metadata to DB
        Evidence evidence = Evidence.create(
                tenantId,
                referenceType,
                referenceId,
                fileName,
                contentType,
                filePath
        );

        return repository.save(evidence);
    }

    private void validateReferenceBelongsToTenant(UUID tenantId, ReferenceType referenceType, UUID referenceId) {
        switch (referenceType) {
            case ASSET -> assetRepositoryPort.findByTenantIdAndId(tenantId, referenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found for tenant"));
            case INCIDENT -> incidentRepositoryPort.findByTenantIdAndId(tenantId, referenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Incident not found for tenant"));
            case WORK_ORDER -> workOrderRepositoryPort.findByTenantIdAndId(tenantId, referenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Work order not found for tenant"));
        }
    }
}
