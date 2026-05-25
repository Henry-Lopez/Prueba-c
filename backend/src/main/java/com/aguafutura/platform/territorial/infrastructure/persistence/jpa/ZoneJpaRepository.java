package com.aguafutura.platform.territorial.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZoneJpaRepository extends JpaRepository<ZoneJpaEntity, UUID> {

    List<ZoneJpaEntity> findByTenantId(UUID tenantId);

    List<ZoneJpaEntity> findByTenantIdAndEnabledTrue(UUID tenantId);

    Optional<ZoneJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    boolean existsByTenantIdAndCode(UUID tenantId, String code);

    boolean existsByTenantIdAndCodeAndIdNot(UUID tenantId, String code, UUID id);
}
