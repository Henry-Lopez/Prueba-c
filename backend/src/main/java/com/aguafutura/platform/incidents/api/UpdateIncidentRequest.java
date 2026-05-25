package com.aguafutura.platform.incidents.api;

import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.incidents.domain.IncidentStatus;

public record UpdateIncidentRequest(
        String title,
        String description,
        IncidentSeverity severity,
        IncidentStatus status
) {
}
