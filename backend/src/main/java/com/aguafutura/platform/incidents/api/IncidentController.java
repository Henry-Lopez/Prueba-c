package com.aguafutura.platform.incidents.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.incidents.application.ListIncidentsUseCase;
import com.aguafutura.platform.incidents.application.ReportIncidentUseCase;
import com.aguafutura.platform.incidents.domain.Incident;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final ReportIncidentUseCase reportIncidentUseCase;
    private final ListIncidentsUseCase listIncidentsUseCase;

    public IncidentController(
            ReportIncidentUseCase reportIncidentUseCase,
            ListIncidentsUseCase listIncidentsUseCase
    ) {
        this.reportIncidentUseCase = reportIncidentUseCase;
        this.listIncidentsUseCase = listIncidentsUseCase;
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

        IncidentResponse response = toResponse(incident);

        return ResponseEntity
                .created(URI.create("/api/v1/incidents/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> list(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        List<IncidentResponse> incidents = listIncidentsUseCase.execute(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(incidents);
    }

    private IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTenantId(),
                incident.getAssetId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getCreatedAt()
        );
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
