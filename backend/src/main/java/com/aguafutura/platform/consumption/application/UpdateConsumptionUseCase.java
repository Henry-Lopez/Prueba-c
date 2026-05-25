package com.aguafutura.platform.consumption.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.consumption.domain.Consumption;
import com.aguafutura.platform.consumption.domain.UnitType;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateConsumptionUseCase {

    private final ConsumptionRepositoryPort consumptionRepositoryPort;
    private final AssetRepositoryPort assetRepositoryPort;
    private final AuditLogPort auditLogPort;

    public UpdateConsumptionUseCase(
            ConsumptionRepositoryPort consumptionRepositoryPort,
            AssetRepositoryPort assetRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        this.consumptionRepositoryPort = consumptionRepositoryPort;
        this.assetRepositoryPort = assetRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public Consumption execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID consumptionId,
            LocalDateTime readingDate,
            BigDecimal value,
            UnitType unit
    ) {
        Consumption existing = consumptionRepositoryPort.findByTenantIdAndId(tenantId, consumptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumption not found"));

        assetRepositoryPort.findByTenantIdAndId(tenantId, existing.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        Consumption saved = consumptionRepositoryPort.save(existing.update(readingDate, value, unit));

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "CONSUMPTION_UPDATED",
                "CONSUMPTION",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }
}
