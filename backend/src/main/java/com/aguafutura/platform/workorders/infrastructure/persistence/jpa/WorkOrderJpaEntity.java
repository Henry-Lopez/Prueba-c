package com.aguafutura.platform.workorders.infrastructure.persistence.jpa;

import com.aguafutura.platform.workorders.domain.WorkOrder;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;
import com.aguafutura.platform.workorders.domain.WorkOrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order")
public class WorkOrderJpaEntity {

    @Id
    private UUID id;
    
    private UUID tenantId;
    private UUID assetId;
    private UUID incidentId;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected WorkOrderJpaEntity() {}

    public static WorkOrderJpaEntity fromDomain(WorkOrder domain) {
        WorkOrderJpaEntity entity = new WorkOrderJpaEntity();
        entity.id = domain.getId();
        entity.tenantId = domain.getTenantId();
        entity.assetId = domain.getAssetId();
        entity.incidentId = domain.getIncidentId();
        entity.description = domain.getDescription();
        entity.status = domain.getStatus().name();
        entity.priority = domain.getPriority().name();
        entity.assignedTo = domain.getAssignedTo();
        entity.scheduledAt = domain.getScheduledAt();
        entity.completedAt = domain.getCompletedAt();
        entity.cancelledAt = domain.getCancelledAt();
        entity.cancelReason = domain.getCancelReason();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }

    public WorkOrder toDomain() {
        return new WorkOrder(
                this.id,
                this.tenantId,
                this.assetId,
                this.incidentId,
                this.description,
                WorkOrderStatus.valueOf(this.status),
                WorkOrderPriority.valueOf(this.priority),
                this.assignedTo,
                this.scheduledAt,
                this.completedAt,
                this.cancelledAt,
                this.cancelReason,
                this.createdAt,
                this.updatedAt
        );
    }
}
