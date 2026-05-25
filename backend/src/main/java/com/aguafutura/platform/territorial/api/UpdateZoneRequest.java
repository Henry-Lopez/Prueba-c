package com.aguafutura.platform.territorial.api;

public record UpdateZoneRequest(
        String code,
        String name,
        String description,
        Boolean enabled
) {
}
