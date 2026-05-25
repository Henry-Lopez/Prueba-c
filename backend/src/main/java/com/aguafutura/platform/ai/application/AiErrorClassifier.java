package com.aguafutura.platform.ai.application;

import com.aguafutura.platform.ai.domain.AiFallbackReason;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

final class AiErrorClassifier {

    private AiErrorClassifier() {
    }

    static AiFallbackReason classify(Throwable throwable) {
        if (containsTimeout(throwable)) {
            return AiFallbackReason.TIMEOUT;
        }

        String details = exceptionDetails(throwable).toLowerCase();
        if (details.contains("insufficient_quota") || details.contains("exceeded your current quota")) {
            return AiFallbackReason.INSUFFICIENT_QUOTA;
        }

        if (details.contains("429") || details.contains("too many requests") || details.contains("rate limit")) {
            return AiFallbackReason.RATE_LIMIT;
        }

        return AiFallbackReason.OPENAI_ERROR;
    }

    static String explanation(AiFallbackReason reason) {
        return switch (reason) {
            case API_KEY_MISSING -> "IA no disponible: OPENAI_API_KEY no configurada.";
            case INSUFFICIENT_QUOTA -> "IA no disponible: cuota insuficiente en OpenAI. Revisa billing/créditos del proyecto.";
            case RATE_LIMIT -> "IA no disponible temporalmente: límite de solicitudes del proveedor de IA.";
            case TIMEOUT -> "IA no disponible temporalmente: timeout o error de conexión.";
            case OPENAI_ERROR -> "IA no disponible temporalmente: error del proveedor de IA.";
        };
    }

    private static boolean containsTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof TimeoutException) {
                return true;
            }

            String className = current.getClass().getName().toLowerCase();
            String message = current.getMessage() == null ? "" : current.getMessage().toLowerCase();
            if (className.contains("timeout") || message.contains("timeout") || message.contains("timed out")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private static String exceptionDetails(Throwable throwable) {
        StringBuilder details = new StringBuilder();
        Throwable current = throwable;
        while (current != null) {
            details.append(current.getClass().getName()).append(' ');
            if (current.getMessage() != null) {
                details.append(current.getMessage()).append(' ');
            }
            current = current.getCause();
        }
        return details.toString();
    }
}
