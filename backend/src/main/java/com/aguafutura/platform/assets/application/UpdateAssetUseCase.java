package com.aguafutura.platform.assets.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.assets.domain.AssetType;
import com.aguafutura.platform.core.application.ConflictException;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;

import java.util.UUID;

public class UpdateAssetUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final ZoneRepositoryPort zoneRepositoryPort;
    private final AuditLogPort auditLogPort;

    public UpdateAssetUseCase(
            AssetRepositoryPort assetRepositoryPort,
            ZoneRepositoryPort zoneRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.zoneRepositoryPort = zoneRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public Asset execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID assetId,
            UUID zoneId,
            String code,
            String name,
            AssetType type,
            String locationDescription,
            Boolean enabled
    ) {
        Asset existing = assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        zoneRepositoryPort.findByTenantIdAndId(tenantId, zoneId)
                .filter(zone -> Boolean.TRUE.equals(zone.getEnabled()))
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        String normalizedCode = normalizeCode(code);
        if (assetRepositoryPort.existsByTenantIdAndCodeAndIdNot(tenantId, normalizedCode, assetId)) {
            throw new ConflictException("Asset code already exists for this tenant");
        }

        Asset saved = assetRepositoryPort.save(existing.update(
                zoneId,
                normalizedCode,
                name,
                type,
                locationDescription,
                enabled
        ));

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "ASSET_UPDATED",
                "ASSET",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Asset code is required");
        }
        return code.trim().toUpperCase();
    }
}
