package com.aguafutura.platform.core.bootstrap;

import org.springframework.web.bind.annotation.*;

@RestController
class SecurityProbeController {

    @PostMapping({
            "/api/v1/assets",
            "/api/v1/incidents"
    })
    String write() {
        return "ok";
    }

    @GetMapping({
            "/api/v1/work-orders/{id}",
            "/api/v1/incidents/{id}",
            "/api/v1/evidence/download/{tenantId}/{fileName}"
    })
    String read() {
        return "ok";
    }

    @PatchMapping("/api/v1/incidents/{id}")
    String patchWrite() {
        return "ok";
    }
}
