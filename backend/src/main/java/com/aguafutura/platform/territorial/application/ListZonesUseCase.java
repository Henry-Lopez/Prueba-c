package com.aguafutura.platform.territorial.application;

import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;

import java.util.List;
import java.util.UUID;

public class ListZonesUseCase {

    private final ZoneRepositoryPort zoneRepositoryPort;

    public ListZonesUseCase(ZoneRepositoryPort zoneRepositoryPort) {
        this.zoneRepositoryPort = zoneRepositoryPort;
    }

    public List<Zone> execute(UUID tenantId) {
        return zoneRepositoryPort.findEnabledByTenantId(tenantId);
    }
}
