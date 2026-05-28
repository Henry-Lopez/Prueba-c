package com.aguafutura.platform.assets.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.core.application.ResourceNotFoundException;

import java.util.UUID;

public class GetAssetUseCase {

    private final AssetRepositoryPort assetRepositoryPort;

    public GetAssetUseCase(AssetRepositoryPort assetRepositoryPort) {
        this.assetRepositoryPort = assetRepositoryPort;
    }

    public Asset execute(UUID tenantId, UUID assetId) {
        return assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
    }
}
