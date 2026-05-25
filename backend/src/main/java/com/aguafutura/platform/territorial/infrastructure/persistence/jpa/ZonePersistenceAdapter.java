package com.aguafutura.platform.territorial.infrastructure.persistence.jpa;

import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ZonePersistenceAdapter implements ZoneRepositoryPort {

    private final ZoneJpaRepository repository;

    public ZonePersistenceAdapter(ZoneJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Zone save(Zone zone) {

        ZoneJpaEntity entity = new ZoneJpaEntity();

        entity.setId(zone.getId());
        entity.setTenantId(zone.getTenantId());
        entity.setCode(zone.getCode());
        entity.setName(zone.getName());
        entity.setDescription(zone.getDescription());
        entity.setEnabled(zone.getEnabled());
        entity.setCreatedAt(zone.getCreatedAt());
        entity.setUpdatedAt(zone.getUpdatedAt());

        return toDomain(repository.save(entity));
    }

    @Override
    public List<Zone> findByTenantId(UUID tenantId) {

        return repository.findByTenantId(tenantId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Zone> findEnabledByTenantId(UUID tenantId) {
        return repository.findByTenantIdAndEnabledTrue(tenantId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Zone> findByTenantIdAndId(UUID tenantId, UUID id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByTenantIdAndCode(UUID tenantId, String code) {
        return repository.existsByTenantIdAndCode(tenantId, code);
    }

    @Override
    public boolean existsByTenantIdAndCodeAndIdNot(UUID tenantId, String code, UUID id) {
        return repository.existsByTenantIdAndCodeAndIdNot(tenantId, code, id);
    }

    private Zone toDomain(ZoneJpaEntity entity) {
        return new Zone(
                entity.getId(),
                entity.getTenantId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
