package com.aguafutura.platform.core.application.port;

import com.aguafutura.platform.core.domain.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepositoryPort {

    Tenant save(Tenant tenant);

    List<Tenant> findAll();

    Optional<Tenant> findById(UUID id);

    boolean existsByCode(String code);
}
