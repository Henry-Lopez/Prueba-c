package com.aguafutura.platform.assets.api;

import com.aguafutura.platform.assets.application.CreateAssetUseCase;
import com.aguafutura.platform.assets.application.DisableAssetUseCase;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.application.UpdateAssetUseCase;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final CreateAssetUseCase createAssetUseCase;
    private final ListAssetsUseCase listAssetsUseCase;
    private final UpdateAssetUseCase updateAssetUseCase;
    private final DisableAssetUseCase disableAssetUseCase;
    private final ZoneRepositoryPort zoneRepositoryPort;

    public AssetController(
            CreateAssetUseCase createAssetUseCase,
            ListAssetsUseCase listAssetsUseCase,
            UpdateAssetUseCase updateAssetUseCase,
            DisableAssetUseCase disableAssetUseCase,
            ZoneRepositoryPort zoneRepositoryPort
    ) {
        this.createAssetUseCase = createAssetUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
        this.updateAssetUseCase = updateAssetUseCase;
        this.disableAssetUseCase = disableAssetUseCase;
        this.zoneRepositoryPort = zoneRepositoryPort;
    }

    @PostMapping
    public ResponseEntity<AssetResponse> create(
            @RequestBody CreateAssetRequest request,
            Authentication authentication
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Asset asset = createAssetUseCase.execute(
                tenantId,
                request.zoneId(),
                request.code(),
                request.name(),
                request.type(),
                request.locationDescription()
        );

        AssetResponse response = toResponse(asset, zonesById(tenantId));

        return ResponseEntity
                .created(URI.create("/api/v1/assets/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> list(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Map<UUID, Zone> zonesById = zonesById(tenantId);
        List<AssetResponse> assets = listAssetsUseCase.execute(tenantId)
                .stream()
                .map(asset -> toResponse(asset, zonesById))
                .toList();

        return ResponseEntity.ok(assets);
    }

    @PatchMapping("/{assetId}")
    public ResponseEntity<AssetResponse> update(
            @PathVariable UUID assetId,
            @RequestBody UpdateAssetRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        Asset asset = updateAssetUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                assetId,
                request.zoneId(),
                request.code(),
                request.name(),
                request.type(),
                request.locationDescription(),
                request.enabled()
        );

        return ResponseEntity.ok(toResponse(asset, zonesById(tenantId)));
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> disable(
            @PathVariable UUID assetId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());

        disableAssetUseCase.execute(
                tenantId,
                actorId(authentication),
                actorRole(authentication),
                correlationId(servletRequest),
                assetId
        );

        return ResponseEntity.noContent().build();
    }

    private AssetResponse toResponse(Asset asset, Map<UUID, Zone> zonesById) {
        Zone zone = zonesById.get(asset.getZoneId());
        return new AssetResponse(
                asset.getId(),
                asset.getTenantId(),
                asset.getZoneId(),
                asset.getCode(),
                asset.getName(),
                asset.getType(),
                asset.getLocationDescription(),
                asset.getEnabled(),
                asset.getCreatedAt(),
                zone != null ? zone.getName() : null,
                zone != null ? zone.getCode() : null,
                asset.displayName()
        );
    }

    private Map<UUID, Zone> zonesById(UUID tenantId) {
        return zoneRepositoryPort.findByTenantId(tenantId)
                .stream()
                .collect(Collectors.toMap(Zone::getId, zone -> zone));
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
