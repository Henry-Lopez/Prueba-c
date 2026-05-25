package com.aguafutura.platform.evidence.api;

import com.aguafutura.platform.evidence.domain.ReferenceType;

import java.util.UUID;

public record ReferenceOptionResponse(
        ReferenceType referenceType,
        UUID referenceId,
        String displayName,
        String secondaryLabel
) {
}
