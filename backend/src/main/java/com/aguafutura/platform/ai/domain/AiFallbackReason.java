package com.aguafutura.platform.ai.domain;

public enum AiFallbackReason {
    API_KEY_MISSING,
    INSUFFICIENT_QUOTA,
    RATE_LIMIT,
    TIMEOUT,
    OPENAI_ERROR
}
