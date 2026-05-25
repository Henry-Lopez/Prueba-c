package com.aguafutura.platform.ai.domain;

public record AnomalyReport(
        boolean isAnomaly,
        String analysis,
        String recommendation,
        boolean aiUsed,
        boolean fallbackUsed,
        AiFallbackReason fallbackReason,
        String explanation
) {
    public AnomalyReport(boolean isAnomaly, String analysis, String recommendation) {
        this(isAnomaly, analysis, recommendation, false, false, null, analysis);
    }
}
