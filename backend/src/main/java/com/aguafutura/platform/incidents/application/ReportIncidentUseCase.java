package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;

import java.util.UUID;

public class ReportIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;
    private final AuditLogPort auditLogPort;

    public ReportIncidentUseCase(IncidentRepositoryPort incidentRepositoryPort, AuditLogPort auditLogPort) {
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public Incident execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID assetId,
            String title,
            String description,
            IncidentSeverity severity
    ) {
        Incident incident = Incident.report(tenantId, assetId, title, description, severity);
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
