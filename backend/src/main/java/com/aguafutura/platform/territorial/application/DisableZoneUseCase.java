package com.aguafutura.platform.territorial.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;

import java.util.UUID;

public class DisableZoneUseCase {

    private final ZoneRepositoryPort zoneRepositoryPort;
    private final AuditLogPort auditLogPort;

    public DisableZoneUseCase(ZoneRepositoryPort zoneRepositoryPort, AuditLogPort auditLogPort) {
        this.zoneRepositoryPort = zoneRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public void execute(UUID tenantId, UUID actorId, String actorRole, String correlationId, UUID zoneId) {
        Zone existing = zoneRepositoryPort.findByTenantIdAndId(tenantId, zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        Zone saved = zoneRepositoryPort.save(existing.disable());

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "ZONE_DISABLED",
                "ZONE",
                saved.getId().toString(),
                correlationId
        ));
    }
}
