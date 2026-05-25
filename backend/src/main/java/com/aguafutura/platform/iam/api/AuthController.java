package com.aguafutura.platform.iam.api;

import jakarta.servlet.http.HttpServletRequest;
import com.aguafutura.platform.iam.application.LoginUseCase;
import com.aguafutura.platform.iam.application.RegisterUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        String token = registerUserUseCase.execute(
                request.tenantId(),
                request.fullName(),
                request.email(),
                request.password(),
                request.role(),
                correlationId(servletRequest)
        );

        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        String token = loginUseCase.execute(
                request.tenantId(),
                request.email(),
                request.password(),
                correlationId(servletRequest)
        );

        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    private String correlationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute("correlationId");
        return correlationId != null ? correlationId.toString() : null;
    }
}
