package com.aguafutura.platform.assets.infrastructure.persistence.jpa;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.assets.domain.AssetType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AssetPersistenceAdapter implements AssetRepositoryPort {

    private final AssetJpaRepository repository;

    public AssetPersistenceAdapter(AssetJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Asset save(Asset asset) {
        AssetJpaEntity entity = new AssetJpaEntity(
                asset.getId(),
                asset.getTenantId(),
                asset.getZoneId(),
                asset.getCode(),
                asset.getName(),
                asset.getType().name(),
                asset.getLocationDescription(),
                asset.getEnabled(),
                asset.getCreatedAt()
        );

        return toDomain(repository.save(entity));
    }

    @Override
    public List<Asset> findByTenantId(UUID tenantId) {
        return repository.findByTenantId(tenantId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Asset> findByTenantIdAndId(UUID tenantId, UUID id) {
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

    private Asset toDomain(AssetJpaEntity entity) {
        return new Asset(
                entity.getId(),
                entity.getTenantId(),
                entity.getZoneId(),
                entity.getCode(),
                entity.getName(),
                AssetType.valueOf(entity.getType()),
                entity.getLocationDescription(),
                entity.getEnabled(),
                entity.getCreatedAt()
        );
    }
}
