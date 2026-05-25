package com.aguafutura.platform.workorders.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.incidents.application.ListIncidentsUseCase;
import com.aguafutura.platform.incidents.domain.Incident;
import com.aguafutura.platform.workorders.application.CreateWorkOrderUseCase;
import com.aguafutura.platform.workorders.application.ListWorkOrdersUseCase;
import com.aguafutura.platform.workorders.domain.WorkOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkOrderController {

    private final CreateWorkOrderUseCase createWorkOrderUseCase;
    private final ListWorkOrdersUseCase listWorkOrdersUseCase;
    private final ListAssetsUseCase listAssetsUseCase;
    private final ListIncidentsUseCase listIncidentsUseCase;

    public WorkOrderController(
            CreateWorkOrderUseCase createWorkOrderUseCase,
            ListWorkOrdersUseCase listWorkOrdersUseCase,
            ListAssetsUseCase listAssetsUseCase,
            ListIncidentsUseCase listIncidentsUseCase
    ) {
        this.createWorkOrderUseCase = createWorkOrderUseCase;
        this.listWorkOrdersUseCase = listWorkOrdersUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
        this.listIncidentsUseCase = listIncidentsUseCase;
    }

    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(
            @RequestBody CreateWorkOrderRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        WorkOrder workOrder = createWorkOrderUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                request.assetId(),
                request.incidentId(),
                request.description(),
                request.priority()
        );

        WorkOrderResponse response = toResponse(workOrder, assetsById(tenantId), incidentsById(tenantId));

        return ResponseEntity
                .created(URI.create("/api/v1/work-orders/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkOrderResponse>> list(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Map<UUID, Asset> assetsById = assetsById(tenantId);
        Map<UUID, Incident> incidentsById = incidentsById(tenantId);
        List<WorkOrderResponse> workOrders = listWorkOrdersUseCase.execute(tenantId)
                .stream()
                .map(workOrder -> toResponse(workOrder, assetsById, incidentsById))
                .toList();

        return ResponseEntity.ok(workOrders);
    }

    private WorkOrderResponse toResponse(
            WorkOrder workOrder,
            Map<UUID, Asset> assetsById,
            Map<UUID, Incident> incidentsById
    ) {
        Asset asset = assetsById.get(workOrder.getAssetId());
        Incident incident = workOrder.getIncidentId() != null ? incidentsById.get(workOrder.getIncidentId()) : null;
        String shortId = workOrder.getId().toString().substring(0, 8).toUpperCase();
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getTenantId(),
                workOrder.getAssetId(),
                workOrder.getIncidentId(),
                workOrder.getDescription(),
                workOrder.getStatus(),
                workOrder.getPriority(),
                workOrder.getAssignedTo(),
                workOrder.getScheduledAt(),
                workOrder.getCompletedAt(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt(),
                asset != null ? asset.getName() : null,
                asset != null ? asset.getCode() : null,
                incident != null ? incident.getTitle() : null,
                "OT-" + shortId + " · " + workOrder.getDescription()
        );
    }

    private Map<UUID, Asset> assetsById(UUID tenantId) {
        return listAssetsUseCase.execute(tenantId)
                .stream()
                .collect(Collectors.toMap(Asset::getId, asset -> asset));
    }

    private Map<UUID, Incident> incidentsById(UUID tenantId) {
        return listIncidentsUseCase.execute(tenantId)
                .stream()
                .collect(Collectors.toMap(Incident::getId, incident -> incident));
    }

    private UUID actorId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private String actorRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse(null);
    }

    private String correlationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute("correlationId");
        return correlationId != null ? correlationId.toString() : null;
    }
}
