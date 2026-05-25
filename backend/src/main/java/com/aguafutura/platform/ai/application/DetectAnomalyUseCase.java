package com.aguafutura.platform.ai.application;

import com.aguafutura.platform.ai.domain.AiFallbackReason;
import com.aguafutura.platform.ai.domain.AnomalyReport;
import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.consumption.domain.Consumption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DetectAnomalyUseCase {

    private static final Logger log = LoggerFactory.getLogger(DetectAnomalyUseCase.class);

    private final ChatModel chatModel;
    private final ConsumptionRepositoryPort consumptionRepository;
    private final boolean openAiApiKeyConfigured;

    public DetectAnomalyUseCase(ChatModel chatModel, ConsumptionRepositoryPort consumptionRepository, String openAiApiKey) {
        this.chatModel = chatModel;
        this.consumptionRepository = consumptionRepository;
        this.openAiApiKeyConfigured = openAiApiKey != null && !openAiApiKey.isBlank();
    }

    public AnomalyReport execute(UUID tenantId, UUID assetId) {
        List<Consumption> consumptions = consumptionRepository.findByTenantIdAndAssetId(tenantId, assetId);

        if (consumptions.isEmpty()) {
            return new AnomalyReport(false, "No hay consumos registrados para analizar.", "Registrar más datos.");
        }

        List<Consumption> recent = consumptions.stream()
                .sorted((c1, c2) -> c2.getReadingDate().compareTo(c1.getReadingDate()))
                .limit(5)
                .collect(Collectors.toList());

        String dataString = recent.stream()
                .map(c -> "Fecha: " + c.getReadingDate() + " - Valor: " + c.getValue() + " " + c.getUnit())
                .collect(Collectors.joining("\n"));

        String systemMessage = """
                Eres un experto analista de infraestructuras de agua.
                Tu tarea es analizar los siguientes consumos recientes de un medidor de agua y determinar si existe una anomalía (ej. una posible fuga o un medidor roto).
                Si el último consumo es excesivamente más alto (o más bajo) que el promedio histórico, debes marcarlo como anomalía.

                Responde EXCLUSIVAMENTE en el siguiente formato (sin markdown ni texto extra):
                ANOMALY_DETECTED=true/false
                ANALYSIS=tu justificación breve
                RECOMMENDATION=tu recomendación
                """;

        String userMessage = "Consumos recientes:\n" + dataString;

        if (!openAiApiKeyConfigured) {
            return fallback(recent, AiFallbackReason.API_KEY_MISSING);
        }

        try {
            String response = chatModel.call(systemMessage + "\n" + userMessage);
            return parseResponse(response);
        } catch (Exception e) {
            return fallback(recent, AiErrorClassifier.classify(e));
        }
    }

    private AnomalyReport parseResponse(String aiResponse) {
        boolean isAnomaly = false;
        String analysis = "Análisis no disponible";
        String recommendation = "Revisión manual recomendada";

        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("ANOMALY_DETECTED=")) {
                isAnomaly = Boolean.parseBoolean(line.substring("ANOMALY_DETECTED=".length()).trim());
            } else if (line.startsWith("ANALYSIS=")) {
                analysis = line.substring("ANALYSIS=".length()).trim();
            } else if (line.startsWith("RECOMMENDATION=")) {
                recommendation = line.substring("RECOMMENDATION=".length()).trim();
            }
        }

        return new AnomalyReport(isAnomaly, analysis, recommendation, true, false, null, analysis);
    }

    private AnomalyReport fallback(List<Consumption> recent, AiFallbackReason reason) {
        log.warn("OpenAI request failed: {}", reason);
        log.info("Using deterministic fallback");
        return simulateAnomalyDetection(recent, reason);
    }

    private AnomalyReport simulateAnomalyDetection(List<Consumption> recent, AiFallbackReason reason) {
        String explanation = AiErrorClassifier.explanation(reason);
        if (recent.size() < 2) {
            return new AnomalyReport(false, explanation, "N/A", false, true, reason, explanation);
        }

        double latest = recent.get(0).getValue().doubleValue();
        double previous = recent.get(1).getValue().doubleValue();

        if (latest > previous * 1.5) {
            return new AnomalyReport(
                    true,
                    explanation,
                    "Enviar un técnico para inspeccionar posibles fugas.",
                    false,
                    true,
                    reason,
                    explanation
            );
        }

        return new AnomalyReport(
                false,
                explanation,
                "Ninguna acción requerida.",
                false,
                true,
                reason,
                explanation
        );
    }
}
