package com.aguafutura.platform.workorders.api;

import com.aguafutura.platform.workorders.domain.WorkOrderPriority;

import java.util.UUID;

public record CreateWorkOrderRequest(
        UUID assetId,
        UUID incidentId,
        String description,
        WorkOrderPriority priority,
        String assignedTo
) {}
