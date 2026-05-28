package com.aguafutura.platform.incidents.infrastructure.persistence.jpa;

import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.incidents.domain.IncidentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class IncidentPersistenceAdapter implements IncidentRepositoryPort {

    private final IncidentJpaRepository repository;

    public IncidentPersistenceAdapter(IncidentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Incident save(Incident incident) {
        IncidentJpaEntity entity = new IncidentJpaEntity(
                incident.getId(),
                incident.getTenantId(),
                incident.getAssetId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity().name(),
                incident.getStatus().name(),
                incident.getReporterUserId(),
                incident.getCreatedAt()
        );

        return toDomain(repository.save(entity));
    }

    @Override
    public List<Incident> findByTenantId(UUID tenantId) {
        return repository.findByTenantId(tenantId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Incident> findByTenantIdAndId(UUID tenantId, UUID id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(this::toDomain);
    }

    private Incident toDomain(IncidentJpaEntity entity) {
        return new Incident(
                entity.getId(),
                entity.getTenantId(),
                entity.getAssetId(),
                entity.getTitle(),
                entity.getDescription(),
                IncidentSeverity.valueOf(entity.getSeverity()),
                IncidentStatus.valueOf(entity.getStatus()),
                entity.getReporterUserId(),
                entity.getCreatedAt()
        );
    }
}
