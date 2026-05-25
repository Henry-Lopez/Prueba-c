package com.aguafutura.platform.assets.api;

import com.aguafutura.platform.assets.application.CreateAssetUseCase;
import com.aguafutura.platform.assets.application.ListAssetsUseCase;
import com.aguafutura.platform.assets.domain.Asset;
import com.aguafutura.platform.territorial.application.port.ZoneRepositoryPort;
import com.aguafutura.platform.territorial.domain.Zone;
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
    private final ZoneRepositoryPort zoneRepositoryPort;

    public AssetController(
            CreateAssetUseCase createAssetUseCase,
            ListAssetsUseCase listAssetsUseCase,
            ZoneRepositoryPort zoneRepositoryPort
    ) {
        this.createAssetUseCase = createAssetUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
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
                asset.getCode() + " · " + asset.getName()
        );
    }

    private Map<UUID, Zone> zonesById(UUID tenantId) {
        return zoneRepositoryPort.findByTenantId(tenantId)
                .stream()
                .collect(Collectors.toMap(Zone::getId, zone -> zone));
    }
}
