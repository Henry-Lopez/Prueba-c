package com.aguafutura.platform.assets.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetJpaRepository extends JpaRepository<AssetJpaEntity, UUID> {

    List<AssetJpaEntity> findByTenantId(UUID tenantId);

    Optional<AssetJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}
