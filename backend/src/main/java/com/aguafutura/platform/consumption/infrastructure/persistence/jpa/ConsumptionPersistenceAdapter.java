package com.aguafutura.platform.consumption.infrastructure.persistence.jpa;

import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.consumption.domain.Consumption;
import com.aguafutura.platform.consumption.domain.UnitType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsumptionPersistenceAdapter implements ConsumptionRepositoryPort {

    private final ConsumptionJpaRepository repository;

    public ConsumptionPersistenceAdapter(ConsumptionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Consumption save(Consumption consumption) {
        ConsumptionJpaEntity entity = new ConsumptionJpaEntity(
                consumption.getId(),
                consumption.getTenantId(),
                consumption.getAssetId(),
                consumption.getReadingDate(),
                consumption.getValue(),
                consumption.getUnit().name(),
                consumption.getOriginalValue(),
                consumption.getOriginalUnit().name(),
                consumption.getValue(),
                consumption.getCreatedAt()
        );

        return toDomain(repository.save(entity));
    }

    @Override
    public List<Consumption> findByTenantIdAndAssetId(UUID tenantId, UUID assetId) {
        return repository.findByTenantIdAndAssetId(tenantId, assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Consumption> findByTenantIdAndId(UUID tenantId, UUID id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(this::toDomain);
    }

    private Consumption toDomain(ConsumptionJpaEntity entity) {
        return new Consumption(
                entity.getId(),
                entity.getTenantId(),
                entity.getAssetId(),
                entity.getReadingDate(),
                entity.getNormalizedVolumeM3() != null ? entity.getNormalizedVolumeM3() : entity.getValue(),
                UnitType.CUBIC_METERS,
                entity.getOriginalValue() != null ? entity.getOriginalValue() : entity.getValue(),
                UnitType.valueOf(entity.getOriginalUnit() != null ? entity.getOriginalUnit() : entity.getUnit()),
                entity.getCreatedAt()
        );
    }
}
