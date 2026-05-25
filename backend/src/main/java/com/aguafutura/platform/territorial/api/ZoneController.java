package com.aguafutura.platform.territorial.api;

import com.aguafutura.platform.territorial.application.DisableZoneUseCase;
import com.aguafutura.platform.territorial.application.CreateZoneUseCase;
import com.aguafutura.platform.territorial.application.ListZonesUseCase;
import com.aguafutura.platform.territorial.application.UpdateZoneUseCase;
import com.aguafutura.platform.territorial.domain.Zone;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/zones")
public class ZoneController {

    private final CreateZoneUseCase createZoneUseCase;
    private final ListZonesUseCase listZonesUseCase;
    private final UpdateZoneUseCase updateZoneUseCase;
    private final DisableZoneUseCase disableZoneUseCase;

    public ZoneController(
            CreateZoneUseCase createZoneUseCase,
            ListZonesUseCase listZonesUseCase,
            UpdateZoneUseCase updateZoneUseCase,
            DisableZoneUseCase disableZoneUseCase
    ) {
        this.createZoneUseCase = createZoneUseCase;
        this.listZonesUseCase = listZonesUseCase;
        this.updateZoneUseCase = updateZoneUseCase;
        this.disableZoneUseCase = disableZoneUseCase;
    }

    @PostMapping
    public ResponseEntity<ZoneResponse> create(
            @RequestBody CreateZoneRequest request,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Zone zone = createZoneUseCase.execute(
                tenantId,
                request.code(),
                request.name(),
                request.description()
        );

        ZoneResponse response = toResponse(zone);

        return ResponseEntity
                .created(URI.create("/api/v1/zones/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ZoneResponse>> list(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        List<ZoneResponse> zones = listZonesUseCase.execute(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(zones);
    }

    @Operation(
            summary = "Edit an existing zone",
            description = "Updates a zone owned by the tenant from the JWT. Code must be 3 to 30 characters using only letters, numbers and hyphens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UpdateZoneRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "ZN-NORTE",
                                      "name": "Zona Norte",
                                      "description": "Sector norte operativo",
                                      "enabled": true
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zone updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid zone code"),
                    @ApiResponse(responseCode = "404", description = "Zone not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicated zone code")
            }
    )
    @PatchMapping("/{zoneId}")
    public ResponseEntity<ZoneResponse> update(
            @PathVariable UUID zoneId,
            @RequestBody UpdateZoneRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Zone zone = updateZoneUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                zoneId,
                request.code(),
                request.name(),
                request.description(),
                request.enabled()
        );

        return ResponseEntity.ok(toResponse(zone));
    }

    @Operation(
            summary = "Disable a zone",
            description = "Soft deletes a zone owned by the tenant from the JWT by setting enabled=false.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zone disabled"),
                    @ApiResponse(responseCode = "404", description = "Zone not found")
            }
    )
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<Void> disable(
            @PathVariable UUID zoneId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        disableZoneUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                zoneId
        );

        return ResponseEntity.noContent().build();
    }

    private ZoneResponse toResponse(Zone zone) {
        return new ZoneResponse(
                zone.getId(),
                zone.getTenantId(),
                zone.getCode(),
                zone.getName(),
                zone.getDescription(),
                zone.getEnabled(),
                zone.getCreatedAt(),
                zone.getUpdatedAt(),
                zone.displayName()
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
