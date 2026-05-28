package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.core.application.ForbiddenOperationException;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;

import java.util.UUID;

public class GetWorkOrderUseCase {

    private final WorkOrderRepositoryPort workOrderRepositoryPort;

    public GetWorkOrderUseCase(WorkOrderRepositoryPort workOrderRepositoryPort) {
        this.workOrderRepositoryPort = workOrderRepositoryPort;
    }

    public WorkOrder execute(UUID tenantId, UUID workOrderId) {
        return workOrderRepositoryPort.findByTenantIdAndId(tenantId, workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
    }

    public WorkOrder executeForTechnician(UUID tenantId, UUID workOrderId, String technicianEmail) {
        WorkOrder workOrder = execute(tenantId, workOrderId);
        String assignedTo = workOrder.getAssignedTo();
        if (technicianEmail == null || assignedTo == null || !assignedTo.equalsIgnoreCase(technicianEmail.trim())) {
            throw new ForbiddenOperationException("Work order is not assigned to the authenticated technician");
        }
        return workOrder;
    }
}
