package com.aguafutura.platform.evidence.api;

import com.aguafutura.platform.evidence.domain.EvidenceType;
import com.aguafutura.platform.evidence.domain.ReferenceType;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvidenceResponse(
        UUID id,
        ReferenceType referenceType,
        UUID referenceId,
        EvidenceType evidenceType,
        String fileName,
        String url,
        LocalDateTime createdAt
) {}
