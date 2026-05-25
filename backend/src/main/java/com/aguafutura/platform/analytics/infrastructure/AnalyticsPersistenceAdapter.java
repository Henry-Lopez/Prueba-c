package com.aguafutura.platform.analytics.infrastructure;

import com.aguafutura.platform.analytics.application.port.AnalyticsRepositoryPort;
import com.aguafutura.platform.analytics.domain.DashboardMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        BigDecimal totalConsumption = jdbcTemplate.queryForObject(
                "SELECT SUM(value) FROM consumption_record WHERE tenant_id = ?",
                BigDecimal.class,
                tenantId
        );
        if (totalConsumption == null) totalConsumption = BigDecimal.ZERO;

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

        // 6. Total Evidence
        Long totalEvidence = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM evidence WHERE tenant_id = ?",
                Long.class,
                tenantId
        );

        return new DashboardMetrics(
                totalAssets != null ? totalAssets : 0L,
                totalConsumption,
                totalIncidents,
                incidentsBySeverity,
                incidentsByStatus,
                totalWorkOrders,
                workOrdersByStatus,
                totalEvidence != null ? totalEvidence : 0L
        );
    }
}
