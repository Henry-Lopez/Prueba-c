package com.aguafutura.platform.incidents.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;

import java.util.UUID;

public class GetIncidentUseCase {

    private final IncidentRepositoryPort incidentRepositoryPort;

    public GetIncidentUseCase(IncidentRepositoryPort incidentRepositoryPort) {
        this.incidentRepositoryPort = incidentRepositoryPort;
    }

    public Incident execute(UUID tenantId, UUID incidentId) {
        return incidentRepositoryPort.findByTenantIdAndId(tenantId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
    }
}
