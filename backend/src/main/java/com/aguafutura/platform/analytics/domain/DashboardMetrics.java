package com.aguafutura.platform.analytics.domain;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardMetrics(
        long totalAssets,
        BigDecimal totalConsumptionVolume,
        long totalIncidents,
        Map<String, Long> incidentsBySeverity,
        Map<String, Long> incidentsByStatus,
        long totalWorkOrders,
        Map<String, Long> workOrdersByStatus,
        long totalEvidence
) {}
