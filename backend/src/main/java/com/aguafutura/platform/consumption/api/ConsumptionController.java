package com.aguafutura.platform.consumption.api;

import com.aguafutura.platform.consumption.application.ListConsumptionsUseCase;
import com.aguafutura.platform.consumption.application.RegisterConsumptionUseCase;
import com.aguafutura.platform.consumption.application.UpdateConsumptionUseCase;
import com.aguafutura.platform.consumption.domain.Consumption;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consumptions")
public class ConsumptionController {

    private final RegisterConsumptionUseCase registerConsumptionUseCase;
    private final ListConsumptionsUseCase listConsumptionsUseCase;
    private final UpdateConsumptionUseCase updateConsumptionUseCase;

    public ConsumptionController(
            RegisterConsumptionUseCase registerConsumptionUseCase,
            ListConsumptionsUseCase listConsumptionsUseCase,
            UpdateConsumptionUseCase updateConsumptionUseCase
    ) {
        this.registerConsumptionUseCase = registerConsumptionUseCase;
        this.listConsumptionsUseCase = listConsumptionsUseCase;
        this.updateConsumptionUseCase = updateConsumptionUseCase;
    }

    @PostMapping
    public ResponseEntity<ConsumptionResponse> register(
            @RequestBody RegisterConsumptionRequest request,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Consumption consumption = registerConsumptionUseCase.execute(
                tenantId,
                request.assetId(),
                request.readingDate(),
                request.value(),
                request.unit()
        );

        ConsumptionResponse response = toResponse(consumption);

        return ResponseEntity
                .created(URI.create("/api/v1/consumptions/" + response.id()))
                .body(response);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<ConsumptionResponse>> listByAsset(
            @PathVariable UUID assetId,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        List<ConsumptionResponse> consumptions = listConsumptionsUseCase.execute(tenantId, assetId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(consumptions);
    }

    @PatchMapping("/{consumptionId}")
    public ResponseEntity<ConsumptionResponse> update(
            @PathVariable UUID consumptionId,
            @RequestBody UpdateConsumptionRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Consumption consumption = updateConsumptionUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                consumptionId,
                request.readingDate(),
                request.value(),
                request.unit()
        );

        return ResponseEntity.ok(toResponse(consumption));
    }

    private ConsumptionResponse toResponse(Consumption consumption) {
        return new ConsumptionResponse(
                consumption.getId(),
                consumption.getTenantId(),
                consumption.getAssetId(),
                consumption.getReadingDate(),
                consumption.getValue(),
                consumption.getUnit(),
                consumption.getCreatedAt()
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
