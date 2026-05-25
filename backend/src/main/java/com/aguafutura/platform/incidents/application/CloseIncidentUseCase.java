package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;

import java.util.UUID;

public class CloseIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;
    private final AuditLogPort auditLogPort;

    public CloseIncidentUseCase(IncidentRepositoryPort incidentRepositoryPort, AuditLogPort auditLogPort) {
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public void execute(UUID tenantId, UUID actorId, String actorRole, String correlationId, UUID incidentId) {
        Incident existing = incidentRepositoryPort.findByTenantIdAndId(tenantId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        Incident saved = incidentRepositoryPort.save(existing.close());

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "INCIDENT_CLOSED",
                "INCIDENT",
                saved.getId().toString(),
                correlationId
        ));
    }
}
