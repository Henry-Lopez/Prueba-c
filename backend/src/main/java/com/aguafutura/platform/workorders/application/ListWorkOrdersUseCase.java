package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;

import java.util.List;
import java.util.UUID;

public class ListWorkOrdersUseCase {

    private final WorkOrderRepositoryPort repository;

    public ListWorkOrdersUseCase(WorkOrderRepositoryPort repository) {
        this.repository = repository;
    }

    public List<WorkOrder> execute(UUID tenantId) {
        return repository.findAllByTenantId(tenantId);
    }

    public List<WorkOrder> executeForTechnician(UUID tenantId, String technicianEmail) {
        if (technicianEmail == null || technicianEmail.isBlank()) {
            return List.of();
        }

        return repository.findAllByTenantIdAndAssignedTo(tenantId, technicianEmail.trim());
    }
}
