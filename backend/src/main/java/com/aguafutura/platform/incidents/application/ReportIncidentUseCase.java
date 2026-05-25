package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;

import java.util.UUID;

public class ReportIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;
    private final AssetRepositoryPort assetRepositoryPort;

    public ReportIncidentUseCase(
            IncidentRepositoryPort incidentRepositoryPort,
            AssetRepositoryPort assetRepositoryPort
    ) {
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.assetRepositoryPort = assetRepositoryPort;
    }

    public Incident execute(
            UUID tenantId,
            UUID assetId,
            String title,
            String description,
            IncidentSeverity severity
    ) {
        assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for tenant"));

        Incident incident = Incident.report(tenantId, assetId, title, description, severity);
        return incidentRepositoryPort.save(incident);
    }
}
