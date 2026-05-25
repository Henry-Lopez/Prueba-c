package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;
import com.aguafutura.platform.workorders.domain.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateWorkOrderUseCase {

    private final WorkOrderRepositoryPort workOrderRepositoryPort;
    private final AuditLogPort auditLogPort;

    public UpdateWorkOrderUseCase(WorkOrderRepositoryPort workOrderRepositoryPort, AuditLogPort auditLogPort) {
        this.workOrderRepositoryPort = workOrderRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public WorkOrder execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID workOrderId,
            String description,
            WorkOrderPriority priority,
            WorkOrderStatus status,
            String assignedTo,
            LocalDateTime scheduledAt
    ) {
        WorkOrder workOrder = workOrderRepositoryPort.findByTenantIdAndId(tenantId, workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));

        workOrder.updateDetails(description, priority, status, assignedTo, scheduledAt);
        WorkOrder saved = workOrderRepositoryPort.save(workOrder);

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "WORKORDER_UPDATED",
                "WORK_ORDER",
                saved.getId().toString(),
                correlationId
        ));

        return saved;
    }
}
