package com.aguafutura.platform.analytics.domain;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardMetrics(
        long totalAssets,
        BigDecimal totalConsumptionVolume,
        BigDecimal totalConsumptionLiters,
        BigDecimal totalConsumptionGallons,
        long consumptionReadingCount,
        Map<String, Long> consumptionReadingsByOriginalUnit,
        Map<String, BigDecimal> consumptionByAsset,
        BigDecimal averageConsumptionPerReading,
        long monitoredAssets,
        long totalIncidents,
        Map<String, Long> incidentsBySeverity,
        Map<String, Long> incidentsByStatus,
        long totalWorkOrders,
        Map<String, Long> workOrdersByStatus,
        Map<String, Long> technicianActiveWorkload,
        long activeWorkOrders,
        long completedWorkOrders,
        long consumptionRelatedIncidents,
        long totalEvidence
) {}
