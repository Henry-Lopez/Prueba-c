package com.aguafutura.platform.territorial.application.port;

import com.aguafutura.platform.territorial.domain.Zone;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZoneRepositoryPort {

    Zone save(Zone zone);

    List<Zone> findByTenantId(UUID tenantId);

    List<Zone> findEnabledByTenantId(UUID tenantId);

    Optional<Zone> findByTenantIdAndId(UUID tenantId, UUID id);

    boolean existsByTenantIdAndCode(UUID tenantId, String code);

    boolean existsByTenantIdAndCodeAndIdNot(UUID tenantId, String code, UUID id);
}
