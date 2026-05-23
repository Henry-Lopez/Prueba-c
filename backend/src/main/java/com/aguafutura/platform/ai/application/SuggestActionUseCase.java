package com.aguafutura.platform.ai.application;

import com.aguafutura.platform.ai.domain.AiSuggestion;
import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.consumption.domain.Consumption;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.core.domain.AuditLog;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SuggestActionUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final IncidentRepositoryPort incidentRepositoryPort;
    private final ConsumptionRepositoryPort consumptionRepositoryPort;
    private final AuditLogPort auditLogPort;

    public SuggestActionUseCase(
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort,
            ConsumptionRepositoryPort consumptionRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.incidentRepositoryPort = incidentRepositoryPort;
        this.consumptionRepositoryPort = consumptionRepositoryPort;
        this.auditLogPort = auditLogPort;
    }

    public AiSuggestion suggestForAsset(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID assetId
    ) {
        assetRepositoryPort.findByTenantId(tenantId)
                .stream()
                .filter(asset -> asset.getId().equals(assetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for tenant"));

        List<Consumption> recent = consumptionRepositoryPort.findByTenantIdAndAssetId(tenantId, assetId)
                .stream()
                .sorted(Comparator.comparing(Consumption::getReadingDate).reversed())
                .limit(2)
                .toList();

        AiSuggestion suggestion;
        if (recent.size() < 2) {
            suggestion = fallback(
                    IncidentSeverity.LOW,
                    WorkOrderPriority.LOW,
                    "No hay suficientes lecturas recientes para inferir riesgo. Se sugiere monitoreo normal."
            );
        } else {
            suggestion = suggestionFromReadings(recent);
        }

        auditSuggestion(tenantId, actorId, actorRole, "ASSET", assetId, correlationId);
        return suggestion;
    }

    public AiSuggestion suggestForIncident(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String correlationId,
            UUID incidentId
    ) {
        Incident incident = incidentRepositoryPort.findByTenantId(tenantId)
                .stream()
                .filter(current -> current.getId().equals(incidentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Incident not found for tenant"));

        WorkOrderPriority prioritySuggestion = switch (incident.getSeverity()) {
            case CRITICAL -> WorkOrderPriority.CRITICAL;
            case HIGH -> WorkOrderPriority.HIGH;
            case MEDIUM -> WorkOrderPriority.MEDIUM;
            case LOW -> WorkOrderPriority.LOW;
        };

        AiSuggestion suggestion = fallback(
                incident.getSeverity(),
                prioritySuggestion,
                "La sugerencia se basa en la severidad actual del incidente. Debe ser revisada por un operador antes de ejecutar acciones."
        );

        auditSuggestion(tenantId, actorId, actorRole, "INCIDENT", incidentId, correlationId);
        return suggestion;
    }

    private AiSuggestion suggestionFromReadings(List<Consumption> recent) {
        double latest = recent.get(0).getValue().doubleValue();
        double previous = recent.get(1).getValue().doubleValue();

        if (previous > 0 && latest >= previous * 2) {
            return fallback(
                    IncidentSeverity.HIGH,
                    WorkOrderPriority.CRITICAL,
                    "La última lectura duplica o supera la lectura anterior. Se sugiere revisión prioritaria por posible fuga o medición anómala."
            );
        }

        if (previous > 0 && latest >= previous * 1.5) {
            return fallback(
                    IncidentSeverity.MEDIUM,
                    WorkOrderPriority.HIGH,
                    "La última lectura supera en al menos 50% la lectura anterior. Se sugiere inspección preventiva."
            );
        }

        return fallback(
                IncidentSeverity.LOW,
                WorkOrderPriority.LOW,
                "Las lecturas recientes no muestran un incremento relevante. Se sugiere continuar con monitoreo normal."
        );
    }

    private AiSuggestion fallback(
            IncidentSeverity severitySuggestion,
            WorkOrderPriority prioritySuggestion,
            String explanation
    ) {
        return new AiSuggestion(
                severitySuggestion,
                prioritySuggestion,
                explanation,
                false,
                true
        );
    }

    private void auditSuggestion(
            UUID tenantId,
            UUID actorId,
            String actorRole,
            String resourceType,
            UUID resourceId,
            String correlationId
    ) {
        auditLogPort.save(AuditLog.create(
                tenantId,
                actorId,
                actorRole,
                "AI_SUGGESTION_GENERATED",
                resourceType,
                resourceId.toString(),
                correlationId
        ));
    }
}
