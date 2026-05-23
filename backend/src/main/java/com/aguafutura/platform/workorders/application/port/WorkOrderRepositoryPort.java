package com.aguafutura.platform.workorders.application.port;

import com.aguafutura.platform.workorders.domain.WorkOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepositoryPort {
    WorkOrder save(WorkOrder workOrder);
    Optional<WorkOrder> findById(UUID id);
    Optional<WorkOrder> findByTenantIdAndId(UUID tenantId, UUID id);
    List<WorkOrder> findAllByTenantId(UUID tenantId);
    List<WorkOrder> findAllByAssetId(UUID assetId);
}
