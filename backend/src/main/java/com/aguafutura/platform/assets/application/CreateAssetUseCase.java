package com.aguafutura.platform.assets.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.assets.domain.AssetType;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;

import java.util.UUID;

public class CreateAssetUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final ZoneRepositoryPort zoneRepositoryPort;

    public CreateAssetUseCase(AssetRepositoryPort assetRepositoryPort, ZoneRepositoryPort zoneRepositoryPort) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.zoneRepositoryPort = zoneRepositoryPort;
    }

    public Asset execute(
            UUID tenantId,
            UUID zoneId,
            String code,
            String name,
            AssetType type,
            String locationDescription
    ) {
        zoneRepositoryPort.findByTenantIdAndId(tenantId, zoneId)
                .filter(zone -> Boolean.TRUE.equals(zone.getEnabled()))
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        Asset asset = Asset.create(
                tenantId,
                zoneId,
                code,
                name,
                type,
                locationDescription
        );

        return assetRepositoryPort.save(asset);
    }
}
