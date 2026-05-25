package com.aguafutura.platform.consumption.application.port;

import com.aguafutura.platform.consumption.domain.Consumption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumptionRepositoryPort {
    Consumption save(Consumption consumption);
    List<Consumption> findByTenantIdAndAssetId(UUID tenantId, UUID assetId);
    Optional<Consumption> findByTenantIdAndId(UUID tenantId, UUID id);
}
