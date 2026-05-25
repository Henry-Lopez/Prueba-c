package com.aguafutura.platform.territorial.api;

public record CreateZoneRequest(
        String code,
        String name,
        String description
) {
}
