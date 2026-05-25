package com.aguafutura.platform.workorders.api;

import com.aguafutura.platform.workorders.domain.WorkOrderPriority;
import com.aguafutura.platform.workorders.domain.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderResponse(
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
        LocalDateTime updatedAt,
        String assetName,
        String assetCode,
        String incidentTitle,
        String displayName
) {}
