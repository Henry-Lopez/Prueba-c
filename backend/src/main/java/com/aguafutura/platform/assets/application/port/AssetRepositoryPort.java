package com.aguafutura.platform.assets.application.port;

import com.aguafutura.platform.assets.domain.Asset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepositoryPort {

    Asset save(Asset asset);

    List<Asset> findByTenantId(UUID tenantId);

    Optional<Asset> findByTenantIdAndId(UUID tenantId, UUID id);

    boolean existsByTenantIdAndCode(UUID tenantId, String code);

    boolean existsByTenantIdAndCodeAndIdNot(UUID tenantId, String code, UUID id);
}
