package com.aguafutura.platform.incidents.application.port;

import com.aguafutura.platform.incidents.domain.Incident;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepositoryPort {
    Incident save(Incident incident);
    List<Incident> findByTenantId(UUID tenantId);
    Optional<Incident> findByTenantIdAndId(UUID tenantId, UUID id);
}
