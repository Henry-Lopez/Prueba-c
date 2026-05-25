package com.aguafutura.platform.workorders.api;

import com.aguafutura.platform.workorders.domain.WorkOrderPriority;
import com.aguafutura.platform.workorders.domain.WorkOrderStatus;

import java.time.LocalDateTime;

public record UpdateWorkOrderRequest(
        String description,
        WorkOrderPriority priority,
        WorkOrderStatus status,
        String assignedTo,
        LocalDateTime scheduledAt
) {
}
