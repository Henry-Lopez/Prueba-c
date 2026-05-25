package com.aguafutura.platform.territorial.api;

import com.aguafutura.platform.core.bootstrap.SecurityConfig;
import com.aguafutura.platform.iam.bootstrap.JwtAuthenticationFilter;
import com.aguafutura.platform.territorial.application.CreateZoneUseCase;
import com.aguafutura.platform.territorial.application.DisableZoneUseCase;
import com.aguafutura.platform.territorial.application.ListZonesUseCase;
import com.aguafutura.platform.territorial.application.UpdateZoneUseCase;
import com.aguafutura.platform.territorial.domain.Zone;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ZoneController.class)
@Import(SecurityConfig.class)
class ZoneControllerSecurityTest {

    private static final UUID TENANT_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CreateZoneUseCase createZoneUseCase;

    @MockBean
    private ListZonesUseCase listZonesUseCase;

    @MockBean
    private UpdateZoneUseCase updateZoneUseCase;

    @MockBean
    private DisableZoneUseCase disableZoneUseCase;

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
    void adminCanEditZone() throws Exception {
        assertCanEdit("ADMIN");
    }

    @Test
    void coordinatorCanEditZone() throws Exception {
        assertCanEdit("COORDINATOR");
    }

    @Test
    void auditorCannotEditZone() throws Exception {
        assertCannotEdit("AUDITOR");
    }

    @Test
    void technicianCannotEditZone() throws Exception {
        assertCannotEdit("TECHNICIAN");
    }

    private void assertCanEdit(String role) throws Exception {
        UUID zoneId = UUID.randomUUID();
        when(updateZoneUseCase.execute(
                eq(TENANT_ID),
                any(UUID.class),
                eq(role),
                any(),
                eq(zoneId),
                eq("ZN-NORTE"),
                eq("Zona Norte"),
                eq("Sector norte operativo"),
                eq(true)
        )).thenReturn(Zone.create(TENANT_ID, "ZN-NORTE", "Zona Norte", "Sector norte operativo"));

        mockMvc.perform(patch("/api/v1/zones/{zoneId}", zoneId)
                        .with(authentication(authenticationFor(role)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ZN-NORTE",
                                  "name": "Zona Norte",
                                  "description": "Sector norte operativo",
                                  "enabled": true
                                }
                                """))
                .andExpect(status().isOk());
    }

    private void assertCannotEdit(String role) throws Exception {
        mockMvc.perform(patch("/api/v1/zones/{zoneId}", UUID.randomUUID())
                        .with(authentication(authenticationFor(role)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "ZN-NORTE",
                                  "name": "Zona Norte",
                                  "description": "Sector norte operativo",
                                  "enabled": true
                                }
                                """))
                .andExpect(status().isForbidden());
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
