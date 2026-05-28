package com.aguafutura.platform.iam.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record TechnicianWorkloadResponse(
        UUID technicianId,
        String fullName,
        String email,
        long pendingOrders,
        long scheduledOrders,
        long inProgressOrders,
        long completedOrders,
        long cancelledOrders,
        long totalAssigned,
        LocalDateTime lastAssignedAt,
        String availability
) {}
