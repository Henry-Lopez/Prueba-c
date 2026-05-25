package com.aguafutura.platform.incidents.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.incidents.application.CloseIncidentUseCase;
import com.aguafutura.platform.incidents.application.ListIncidentsUseCase;
import com.aguafutura.platform.incidents.application.ReportIncidentUseCase;
import com.aguafutura.platform.incidents.application.UpdateIncidentUseCase;
import com.aguafutura.platform.incidents.domain.Incident;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final ReportIncidentUseCase reportIncidentUseCase;
    private final ListIncidentsUseCase listIncidentsUseCase;
    private final ListAssetsUseCase listAssetsUseCase;
    private final UpdateIncidentUseCase updateIncidentUseCase;
    private final CloseIncidentUseCase closeIncidentUseCase;

    public IncidentController(
            ReportIncidentUseCase reportIncidentUseCase,
            ListIncidentsUseCase listIncidentsUseCase,
            ListAssetsUseCase listAssetsUseCase,
            UpdateIncidentUseCase updateIncidentUseCase,
            CloseIncidentUseCase closeIncidentUseCase
    ) {
        this.reportIncidentUseCase = reportIncidentUseCase;
        this.listIncidentsUseCase = listIncidentsUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
        this.updateIncidentUseCase = updateIncidentUseCase;
        this.closeIncidentUseCase = closeIncidentUseCase;
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> report(
            @RequestBody ReportIncidentRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Incident incident = reportIncidentUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                request.assetId(),
                request.title(),
                request.description(),
                request.severity()
        );

        IncidentResponse response = toResponse(incident, assetsById(tenantId));

        return ResponseEntity
                .created(URI.create("/api/v1/incidents/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> list(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Map<UUID, Asset> assetsById = assetsById(tenantId);
        List<IncidentResponse> incidents = listIncidentsUseCase.execute(tenantId)
                .stream()
                .map(incident -> toResponse(incident, assetsById))
                .toList();

        return ResponseEntity.ok(incidents);
    }

    @PatchMapping("/{incidentId}")
    public ResponseEntity<IncidentResponse> update(
            @PathVariable UUID incidentId,
            @RequestBody UpdateIncidentRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Incident incident = updateIncidentUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                incidentId,
                request.title(),
                request.description(),
                request.severity(),
                request.status()
        );

        return ResponseEntity.ok(toResponse(incident, assetsById(tenantId)));
    }

    @DeleteMapping("/{incidentId}")
    public ResponseEntity<Void> close(
            @PathVariable UUID incidentId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        closeIncidentUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                incidentId
        );

        return ResponseEntity.noContent().build();
    }

    private IncidentResponse toResponse(Incident incident, Map<UUID, Asset> assetsById) {
        Asset asset = assetsById.get(incident.getAssetId());
        String assetLabel = asset != null ? asset.getCode() : null;
        return new IncidentResponse(
                incident.getId(),
                incident.getTenantId(),
                incident.getAssetId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getCreatedAt(),
                asset != null ? asset.getName() : null,
                asset != null ? asset.getCode() : null,
                assetLabel != null ? incident.getTitle() + " · " + assetLabel : incident.getTitle()
        );
    }

    private Map<UUID, Asset> assetsById(UUID tenantId) {
        return listAssetsUseCase.execute(tenantId)
                .stream()
                .collect(Collectors.toMap(Asset::getId, asset -> asset));
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
