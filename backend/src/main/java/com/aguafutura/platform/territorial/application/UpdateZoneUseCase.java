package com.aguafutura.platform.territorial.application;

import com.aguafutura.platform.core.application.ConflictException;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;

import java.util.UUID;

public class UpdateZoneUseCase {

    private final ZoneRepositoryPort zoneRepositoryPort;
    private final AuditLogPort auditLogPort;

    public UpdateZoneUseCase(ZoneRepositoryPort zoneRepositoryPort, AuditLogPort auditLogPort) {
        this.zoneRepositoryPort = zoneRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public Zone execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID zoneId,
            String code,
            String name,
            String description,
            Boolean enabled
    ) {
        Zone existing = zoneRepositoryPort.findByTenantIdAndId(tenantId, zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        String normalizedCode = Zone.normalizeCode(code);
        if (zoneRepositoryPort.existsByTenantIdAndCodeAndIdNot(tenantId, normalizedCode, zoneId)) {
            throw new ConflictException("Zone code already exists for this tenant");
        }

        Zone saved = zoneRepositoryPort.save(existing.update(normalizedCode, name, description, enabled));

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "ZONE_UPDATED",
                "ZONE",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }
}
