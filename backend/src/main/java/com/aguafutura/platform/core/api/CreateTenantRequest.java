package com.aguafutura.platform.core.api;

public record CreateTenantRequest(
        String code,
        String name
) {}
