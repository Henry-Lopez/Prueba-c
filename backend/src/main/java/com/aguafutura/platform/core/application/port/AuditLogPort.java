package com.aguafutura.platform.core.application.port;

import com.aguafutura.platform.core.domain.AuditLog;

public interface AuditLogPort {
    AuditLog save(AuditLog auditLog);
}
