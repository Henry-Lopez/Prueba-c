package com.aguafutura.platform.ai.api;

import com.aguafutura.platform.ai.application.DetectAnomalyUseCase;
import com.aguafutura.platform.ai.application.SuggestActionUseCase;
import com.aguafutura.platform.ai.domain.AiSuggestion;
import com.aguafutura.platform.ai.domain.AnomalyReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final DetectAnomalyUseCase detectAnomalyUseCase;
    private final SuggestActionUseCase suggestActionUseCase;

    public AiController(
            DetectAnomalyUseCase detectAnomalyUseCase,
            SuggestActionUseCase suggestActionUseCase
    ) {
        this.detectAnomalyUseCase = detectAnomalyUseCase;
        this.suggestActionUseCase = suggestActionUseCase;
    }

    @GetMapping("/analyze/{assetId}")
    public ResponseEntity<AnomalyReport> analyzeAsset(@PathVariable UUID assetId, Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());
        AnomalyReport report = detectAnomalyUseCase.execute(tenantId, assetId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/suggestions/assets/{assetId}")
    public ResponseEntity<AiSuggestion> suggestForAsset(@PathVariable UUID assetId, Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());
        AiSuggestion suggestion = suggestActionUseCase.suggestForAsset(tenantId, assetId);
        return ResponseEntity.ok(suggestion);
    }

    @GetMapping("/suggestions/incidents/{incidentId}")
    public ResponseEntity<AiSuggestion> suggestForIncident(@PathVariable UUID incidentId, Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getDetails().toString());
        AiSuggestion suggestion = suggestActionUseCase.suggestForIncident(tenantId, incidentId);
        return ResponseEntity.ok(suggestion);
    }
}
