package com.aguafutura.platform.workorders.infrastructure.persistence.jpa;

import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WorkOrderPersistenceAdapter implements WorkOrderRepositoryPort {

    private final WorkOrderJpaRepository repository;

    public WorkOrderPersistenceAdapter(WorkOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderJpaEntity entity = WorkOrderJpaEntity.fromDomain(workOrder);
        WorkOrderJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<WorkOrder> findById(UUID id) {
        return repository.findById(id).map(WorkOrderJpaEntity::toDomain);
    }

    @Override
    public Optional<WorkOrder> findByTenantIdAndId(UUID tenantId, UUID id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(WorkOrderJpaEntity::toDomain);
    }

    @Override
    public List<WorkOrder> findAllByTenantId(UUID tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(WorkOrderJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> findAllByAssetId(UUID assetId) {
        return repository.findByAssetIdOrderByCreatedAtDesc(assetId).stream()
                .map(WorkOrderJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}
