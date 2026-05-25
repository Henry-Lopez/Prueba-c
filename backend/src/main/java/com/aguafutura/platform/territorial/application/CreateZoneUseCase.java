package com.aguafutura.platform.territorial.application;

import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;

import java.util.UUID;

public class CreateZoneUseCase {

    private final ZoneRepositoryPort zoneRepositoryPort;

    public CreateZoneUseCase(ZoneRepositoryPort zoneRepositoryPort) {
        this.zoneRepositoryPort = zoneRepositoryPort;
    }

    public Zone execute(UUID tenantId, String code, String name) {
        return execute(tenantId, code, name, null);
    }

    public Zone execute(UUID tenantId, String code, String name, String description) {
        String normalizedCode = Zone.normalizeCode(code);
        if (zoneRepositoryPort.existsByTenantIdAndCode(tenantId, normalizedCode)) {
            throw new com.aguafutura.platform.core.application.ConflictException("Zone code already exists for this tenant");
        }

        return zoneRepositoryPort.save(Zone.create(tenantId, normalizedCode, name, description));
    }
}
