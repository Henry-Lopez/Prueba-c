package com.aguafutura.platform.analytics.infrastructure;

import com.aguafutura.platform.analytics.application.port.AnalyticsRepositoryPort;
import com.aguafutura.platform.analytics.domain.DashboardMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AnalyticsPersistenceAdapter implements AnalyticsRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DashboardMetrics getMetricsForTenant(UUID tenantId) {
        // 1. Total Assets
        Long totalAssets = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM water_asset WHERE tenant_id = ?",
                Long.class,
                tenantId
        );

        // 2. Total Consumption Volume
        String normalizedExpression = """
                COALESCE(
                    normalized_volume_m3,
                    CASE
                        WHEN unit = 'LITERS' THEN value / 1000
                        WHEN unit = 'GALLONS' THEN value * 0.00378541
                        ELSE value
                    END
                )
                """;

        BigDecimal totalConsumption = jdbcTemplate.queryForObject(
                "SELECT SUM(" + normalizedExpression + ") FROM consumption_record WHERE tenant_id = ?",
                BigDecimal.class,
                tenantId
        );
        if (totalConsumption == null) totalConsumption = BigDecimal.ZERO;
        BigDecimal totalConsumptionLiters = totalConsumption.multiply(BigDecimal.valueOf(1000));
        BigDecimal totalConsumptionGallons = totalConsumption.divide(BigDecimal.valueOf(0.00378541), 4, RoundingMode.HALF_UP);

        Long consumptionReadingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM consumption_record WHERE tenant_id = ?",
                Long.class,
                tenantId
        );
        long readings = consumptionReadingCount != null ? consumptionReadingCount : 0L;
        BigDecimal averageConsumptionPerReading = readings == 0
                ? BigDecimal.ZERO
                : totalConsumption.divide(BigDecimal.valueOf(readings), 4, RoundingMode.HALF_UP);

        Map<String, Long> consumptionReadingsByOriginalUnit = new HashMap<>();
        jdbcTemplate.query(
                "SELECT COALESCE(original_unit, unit) AS unit_name, COUNT(*) AS cnt FROM consumption_record WHERE tenant_id = ? GROUP BY COALESCE(original_unit, unit)",
                (RowCallbackHandler) rs -> consumptionReadingsByOriginalUnit.put(rs.getString("unit_name"), rs.getLong("cnt")),
                tenantId
        );

        Map<String, BigDecimal> consumptionByAsset = new HashMap<>();
        jdbcTemplate.query(
                """
                SELECT asset.code || ' - ' || asset.name AS asset_label,
                       SUM(COALESCE(consumption.normalized_volume_m3,
                           CASE
                               WHEN consumption.unit = 'LITERS' THEN consumption.value / 1000
                               WHEN consumption.unit = 'GALLONS' THEN consumption.value * 0.00378541
                               ELSE consumption.value
                           END)) AS total_m3
                FROM consumption_record consumption
                JOIN water_asset asset ON asset.id = consumption.asset_id
                WHERE consumption.tenant_id = ?
                GROUP BY asset.code, asset.name
                ORDER BY total_m3 DESC
                """,
                (RowCallbackHandler) rs -> consumptionByAsset.put(rs.getString("asset_label"), rs.getBigDecimal("total_m3")),
                tenantId
        );

        Long monitoredAssets = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT asset_id) FROM consumption_record WHERE tenant_id = ?",
                Long.class,
                tenantId
        );

        // 3. Incidents by Severity
        Map<String, Long> incidentsBySeverity = new HashMap<>();
        jdbcTemplate.query(
                "SELECT severity, COUNT(*) as cnt FROM incident_record WHERE tenant_id = ? GROUP BY severity",
                rs -> {
                    incidentsBySeverity.put(rs.getString("severity"), rs.getLong("cnt"));
                },
                tenantId
        );
        long totalIncidents = incidentsBySeverity.values().stream().mapToLong(Long::longValue).sum();

        // 4. Incidents by Status
        Map<String, Long> incidentsByStatus = new HashMap<>();
        jdbcTemplate.query(
                "SELECT status, COUNT(*) as cnt FROM incident_record WHERE tenant_id = ? GROUP BY status",
                rs -> {
                    incidentsByStatus.put(rs.getString("status"), rs.getLong("cnt"));
                },
                tenantId
        );

        // 5. Work Orders by Status
        Map<String, Long> workOrdersByStatus = new HashMap<>();
        jdbcTemplate.query(
                "SELECT status, COUNT(*) as cnt FROM work_order WHERE tenant_id = ? GROUP BY status",
                rs -> {
                    workOrdersByStatus.put(rs.getString("status"), rs.getLong("cnt"));
                },
                tenantId
        );
        long totalWorkOrders = workOrdersByStatus.values().stream().mapToLong(Long::longValue).sum();
        long activeWorkOrders = workOrdersByStatus.getOrDefault("PENDING", 0L)
                + workOrdersByStatus.getOrDefault("SCHEDULED", 0L)
                + workOrdersByStatus.getOrDefault("IN_PROGRESS", 0L);
        long completedWorkOrders = workOrdersByStatus.getOrDefault("COMPLETED", 0L);

        Map<String, Long> technicianActiveWorkload = new HashMap<>();
        jdbcTemplate.query(
                """
                SELECT assigned_to, COUNT(*) AS cnt
                FROM work_order
                WHERE tenant_id = ?
                  AND assigned_to IS NOT NULL
                  AND status IN ('PENDING', 'SCHEDULED', 'IN_PROGRESS')
                GROUP BY assigned_to
                ORDER BY cnt DESC
                """,
                (RowCallbackHandler) rs -> technicianActiveWorkload.put(rs.getString("assigned_to"), rs.getLong("cnt")),
                tenantId
        );

        Long consumptionRelatedIncidents = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(DISTINCT incident.id)
                FROM incident_record incident
                JOIN consumption_record consumption
                  ON consumption.tenant_id = incident.tenant_id
                 AND consumption.asset_id = incident.asset_id
                WHERE incident.tenant_id = ?
                """,
                Long.class,
                tenantId
        );

        // 6. Total Evidence
        Long totalEvidence = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM evidence WHERE tenant_id = ?",
                Long.class,
                tenantId
        );

        return new DashboardMetrics(
                totalAssets != null ? totalAssets : 0L,
                totalConsumption,
                totalConsumptionLiters,
                totalConsumptionGallons,
                readings,
                consumptionReadingsByOriginalUnit,
                consumptionByAsset,
                averageConsumptionPerReading,
                monitoredAssets != null ? monitoredAssets : 0L,
                totalIncidents,
                incidentsBySeverity,
                incidentsByStatus,
                totalWorkOrders,
                workOrdersByStatus,
                technicianActiveWorkload,
                activeWorkOrders,
                completedWorkOrders,
                consumptionRelatedIncidents != null ? consumptionRelatedIncidents : 0L,
                totalEvidence != null ? totalEvidence : 0L
        );
    }
}
