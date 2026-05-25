package com.aguafutura.platform.assets.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;

import java.util.UUID;

public class DisableAssetUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final AuditLogPort auditLogPort;

    public DisableAssetUseCase(AssetRepositoryPort assetRepositoryPort, AuditLogPort auditLogPort) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public void execute(UUID tenantId, UUID actorId, String actorRole, String correlationId, UUID assetId) {
        Asset existing = assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Asset saved = assetRepositoryPort.save(existing.disable());

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "ASSET_DISABLED",
                "ASSET",
                saved.getId().toString(),
                correlationId
        ));
    }
}
