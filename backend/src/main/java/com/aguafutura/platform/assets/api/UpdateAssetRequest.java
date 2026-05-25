package com.aguafutura.platform.assets.api;

import com.aguafutura.platform.assets.domain.AssetType;

import java.util.UUID;

public record UpdateAssetRequest(
        UUID zoneId,
        String code,
        String name,
        AssetType type,
        String locationDescription,
        Boolean enabled
) {
}
