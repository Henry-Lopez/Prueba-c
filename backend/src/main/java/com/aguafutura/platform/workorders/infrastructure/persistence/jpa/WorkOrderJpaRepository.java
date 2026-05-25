package com.aguafutura.platform.workorders.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderJpaEntity, UUID> {
    Optional<WorkOrderJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
    List<WorkOrderJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<WorkOrderJpaEntity> findByAssetIdOrderByCreatedAtDesc(UUID assetId);
}
