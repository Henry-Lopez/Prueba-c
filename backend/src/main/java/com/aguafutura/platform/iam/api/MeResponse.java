package com.aguafutura.platform.iam.api;

import java.util.List;

public record MeResponse(
        String userId,
        String tenantId,
        List<String> roles,
        String tenantShortId,
        String tenantName
) {
}
