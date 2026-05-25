package com.aguafutura.platform.ai.domain;

import com.aguafutura.platform.incidents.domain.IncidentSeverity;
import com.aguafutura.platform.workorders.domain.WorkOrderPriority;

public record AiSuggestion(
        IncidentSeverity severitySuggestion,
        WorkOrderPriority prioritySuggestion,
        String explanation,
        boolean aiUsed,
        boolean fallbackUsed,
        AiFallbackReason fallbackReason
) {
    public AiSuggestion(
            IncidentSeverity severitySuggestion,
            WorkOrderPriority prioritySuggestion,
            String explanation,
            boolean aiUsed,
            boolean fallbackUsed
    ) {
        this(severitySuggestion, prioritySuggestion, explanation, aiUsed, fallbackUsed, null);
    }
}
