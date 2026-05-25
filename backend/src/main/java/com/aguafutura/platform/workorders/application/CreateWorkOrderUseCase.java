package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.core.application.ResourceNotFoundException;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;

import java.util.UUID;

public class CreateWorkOrderUseCase {

    private final WorkOrderRepositoryPort repository;
    private final AuditLogPort auditLogPort;
    private final AssetRepositoryPort assetRepositoryPort;
    private final IncidentRepositoryPort incidentRepositoryPort;

    public CreateWorkOrderUseCase(
            WorkOrderRepositoryPort repository,
            AuditLogPort auditLogPort,
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort
    ) {
        this.repository = repository;
        this.auditLogPort = auditLogPort;
        this.assetRepositoryPort = assetRepositoryPort;
        this.incidentRepositoryPort = incidentRepositoryPort;
    }

    public WorkOrder execute(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID assetId,
            UUID incidentId,
            String description,
            WorkOrderPriority priority
    ) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID is required to create a Work Order");
        }
        assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .filter(asset -> Boolean.TRUE.equals(asset.getEnabled()))
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        if (incidentId != null) {
            incidentRepositoryPort.findByTenantIdAndId(tenantId, incidentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
        }

        WorkOrder workOrder = WorkOrder.create(
                tenantId,
                assetId,
                incidentId,
                description,
                priority
        );

        WorkOrder savedWorkOrder = repository.save(workOrder);

        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "WORKORDER_CREATED",
                "WORK_ORDER",
                savedWorkOrder.getId().toString(),
                correlationId
        ));

        return savedWorkOrder;
    }
}
