package com.aguafutura.platform.workorders.application;

import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.workorders.application.port.WorkOrderRepositoryPort;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;

import java.util.UUID;

public class CreateWorkOrderUseCase {

    private final WorkOrderRepositoryPort repository;
    private final AssetRepositoryPort assetRepositoryPort;
    private final IncidentRepositoryPort incidentRepositoryPort;

    public CreateWorkOrderUseCase(
            WorkOrderRepositoryPort repository,
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort
    ) {
        this.repository = repository;
        this.assetRepositoryPort = assetRepositoryPort;
        this.incidentRepositoryPort = incidentRepositoryPort;
    }

    public WorkOrder execute(
            UUID tenantId,
            UUID assetId,
            UUID incidentId,
            String description,
            WorkOrderPriority priority
    ) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID is required to create a Work Order");
        }

        assetRepositoryPort.findByTenantIdAndId(tenantId, assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for tenant"));

        if (incidentId != null) {
            Incident incident = incidentRepositoryPort.findByTenantIdAndId(tenantId, incidentId)
                    .orElseThrow(() -> new IllegalArgumentException("Incident not found for tenant"));

            if (!incident.getAssetId().equals(assetId)) {
                throw new IllegalArgumentException("Incident does not belong to the requested asset");
            }
        }

        WorkOrder workOrder = WorkOrder.create(
                tenantId,
                assetId,
                incidentId,
                description,
                priority
        );

        return repository.save(workOrder);
    }
}
