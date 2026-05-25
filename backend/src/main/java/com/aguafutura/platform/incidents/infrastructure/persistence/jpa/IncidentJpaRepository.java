package com.aguafutura.platform.incidents.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, UUID> {
    List<IncidentJpaEntity> findByTenantId(UUID tenantId);
    Optional<IncidentJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
}
