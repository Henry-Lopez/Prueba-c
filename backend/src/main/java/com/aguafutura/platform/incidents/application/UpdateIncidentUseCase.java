package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.incidents.domain.IncidentStatus;

import java.util.UUID;

public class UpdateIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;
    private final AuditLogPort auditLogPort;

    public UpdateIncidentUseCase(IncidentRepositoryPort incidentRepositoryPort, AuditLogPort auditLogPort) {
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public Incident execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID incidentId,
            String title,
            String description,
            IncidentSeverity severity,
            IncidentStatus status
    ) {
        Incident existing = incidentRepositoryPort.findByTenantIdAndId(tenantId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        Incident saved = incidentRepositoryPort.save(existing.update(title, description, severity, status));

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "INCIDENT_UPDATED",
                "INCIDENT",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }
}
