package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;

import java.util.UUID;

public class CancelWorkOrderUseCase {

    private final WorkOrderRepositoryPort workOrderRepositoryPort;
    private final AuditLogPort auditLogPort;

    public CancelWorkOrderUseCase(WorkOrderRepositoryPort workOrderRepositoryPort, AuditLogPort auditLogPort) {
        this.workOrderRepositoryPort = workOrderRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public void execute(UUID tenantId, UUID actorId, String actorRole, String correlationId, UUID workOrderId) {
        WorkOrder workOrder = workOrderRepositoryPort.findByTenantIdAndId(tenantId, workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));

        workOrder.cancel();
        WorkOrder saved = workOrderRepositoryPort.save(workOrder);

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "WORKORDER_CANCELLED",
                "WORK_ORDER",
                saved.getId().toString(),
                correlationId
        ));
    }
}
