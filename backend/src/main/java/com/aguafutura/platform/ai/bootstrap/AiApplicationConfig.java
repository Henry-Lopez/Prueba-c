package com.aguafutura.platform.ai.bootstrap;

import com.aguafutura.platform.ai.application.DetectAnomalyUseCase;
import com.aguafutura.platform.ai.application.SuggestActionUseCase;
import com.aguafutura.platform.assets.application.port.AssetRepositoryPort;
import com.aguafutura.platform.consumption.application.port.ConsumptionRepositoryPort;
import com.aguafutura.platform.core.application.port.AuditLogPort;
import com.aguafutura.platform.incidents.application.port.IncidentRepositoryPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiApplicationConfig {

    @Bean
    public DetectAnomalyUseCase detectAnomalyUseCase(
            ChatModel chatModel,
            ConsumptionRepositoryPort consumptionRepositoryPort,
            @Value("${spring.ai.openai.api-key:}") String openAiApiKey
    ) {
        return new DetectAnomalyUseCase(chatModel, consumptionRepositoryPort, openAiApiKey);
    }

    @Bean
    public SuggestActionUseCase suggestActionUseCase(
            AssetRepositoryPort assetRepositoryPort,
            IncidentRepositoryPort incidentRepositoryPort,
            ConsumptionRepositoryPort consumptionRepositoryPort,
            AuditLogPort auditLogPort
    ) {
        return new SuggestActionUseCase(
                assetRepositoryPort,
                incidentRepositoryPort,
                consumptionRepositoryPort,
                auditLogPort
        );
    }
}
