package com.aguafutura.platform.workorders.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkOrder {

    private final UUID id;
    private final UUID tenantId;
    private final UUID assetId;
    private final UUID incidentId;
    
    private String description;
    private WorkOrderStatus status;
    private WorkOrderPriority priority;
    private String assignedTo;
    
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkOrder(
            UUID id,
            UUID tenantId,
            UUID assetId,
            UUID incidentId,
            String description,
            WorkOrderStatus status,
            WorkOrderPriority priority,
            String assignedTo,
            LocalDateTime scheduledAt,
            LocalDateTime completedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (tenantId == null) throw new IllegalArgumentException("tenantId cannot be null");
        if (assetId == null) throw new IllegalArgumentException("assetId cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("description cannot be blank");
        if (status == null) throw new IllegalArgumentException("status cannot be null");
        if (priority == null) throw new IllegalArgumentException("priority cannot be null");

        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.incidentId = incidentId;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.scheduledAt = scheduledAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static WorkOrder create(
            UUID tenantId,
            UUID assetId,
            UUID incidentId,
            String description,
            WorkOrderPriority priority
    ) {
        return new WorkOrder(
                UUID.randomUUID(),
                tenantId,
                assetId,
                incidentId,
                description,
                WorkOrderStatus.PENDING,
                priority,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void assignTo(String technician) {
        this.assignedTo = technician;
        this.updatedAt = LocalDateTime.now();
    }

    public void startWork() {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot start a work order that is already completed or cancelled");
        }
        this.status = WorkOrderStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a work order that is already completed or cancelled");
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed work order");
        }
        this.status = WorkOrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(
            String description,
            WorkOrderPriority priority,
            WorkOrderStatus status,
            String assignedTo,
            LocalDateTime scheduledAt
    ) {
        if (description == null || description.isBlank()) throw new IllegalArgumentException("description cannot be blank");
        if (priority == null) throw new IllegalArgumentException("priority cannot be null");
        if (status == null) throw new IllegalArgumentException("status cannot be null");

        this.description = description.trim();
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo == null || assignedTo.isBlank() ? null : assignedTo.trim();
        this.scheduledAt = scheduledAt;
        if (status == WorkOrderStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getAssetId() { return assetId; }
    public UUID getIncidentId() { return incidentId; }
    public String getDescription() { return description; }
    public WorkOrderStatus getStatus() { return status; }
    public WorkOrderPriority getPriority() { return priority; }
    public String getAssignedTo() { return assignedTo; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
