package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;

import java.util.UUID;

public class ReportIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;
    private final AuditLogPort auditLogPort;
    private final AssetRepositoryPort assetRepositoryPort;

    public ReportIncidentUseCase(
            IncidentRepositoryPort incidentRepositoryPort,
            AuditLogPort auditLogPort,
            AssetRepositoryPort assetRepositoryPort
    ) {
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.auditLogPort = auditLogPort;
        this.assetRepositoryPort = assetRepositoryPort;
    }

    public Incident execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID assetId,
            String title,
            String description,
            IncidentSeverity severity,
            UUID reporterUserId
    ) {
        UUID effectiveAssetId = assetId;
        if (effectiveAssetId == null && "CITIZEN".equals(actorRole)) {
            effectiveAssetId = assetRepositoryPort.findByTenantId(tenantId)
                    .stream()
                    .filter(asset -> Boolean.TRUE.equals(asset.getEnabled()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found"))
                    .getId();
        }
        assetRepositoryPort.findByTenantIdAndId(tenantId, effectiveAssetId)
                .filter(asset -> Boolean.TRUE.equals(asset.getEnabled()))
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Incident incident = Incident.report(tenantId, effectiveAssetId, title, description, severity, reporterUserId);
        Incident savedIncident = incidentRepositoryPort.save(incident);

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "INCIDENT_CREATED",
                "INCIDENT",
                savedIncident.getId().toString(),
                correlationId
        ));

        return savedIncident;
    }
}
