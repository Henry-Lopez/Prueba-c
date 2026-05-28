package com.aguafutura.platform.core.bootstrap;

import com.aguafutura.platform.iam.bootstrap.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityProbeController.class)
@Import(SecurityConfig.class)
class MvpSecurityPolicyTest {

    private static final UUID TENANT_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void allowJwtFilterToContinue() throws Exception {
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void adminCanAccessOperationalWrites() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .with(authentication(authenticationFor("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void coordinatorCanAccessOperationalWrites() throws Exception {
        mockMvc.perform(post("/api/v1/incidents")
                        .with(authentication(authenticationFor("COORDINATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void technicianCannotCreateAssetsOrIncidents() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .with(authentication(authenticationFor("TECHNICIAN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/incidents")
                        .with(authentication(authenticationFor("TECHNICIAN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void technicianCanReadWorkOrders() throws Exception {
        mockMvc.perform(get("/api/v1/work-orders/{workOrderId}", UUID.randomUUID())
                        .with(authentication(authenticationFor("TECHNICIAN"))))
                .andExpect(status().isOk());
    }

    @Test
    void auditorCanReadButCannotWrite() throws Exception {
        mockMvc.perform(get("/api/v1/incidents/{incidentId}", UUID.randomUUID())
                        .with(authentication(authenticationFor("AUDITOR"))))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/incidents/{incidentId}", UUID.randomUUID())
                        .with(authentication(authenticationFor("AUDITOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void directEvidenceDownloadRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/evidence/download/{tenantId}/{fileName}", TENANT_ID, "file.png"))
                .andExpect(status().isUnauthorized());
    }

    private UsernamePasswordAuthenticationToken authenticationFor(String role) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                UUID.randomUUID().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        authentication.setDetails(TENANT_ID.toString());
        return authentication;
    }
}
