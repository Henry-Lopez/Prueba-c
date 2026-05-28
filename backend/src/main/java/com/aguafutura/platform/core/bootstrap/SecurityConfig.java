package com.aguafutura.platform.core.bootstrap;

import com.aguafutura.platform.iam.bootstrap.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/health",
                                "/api/v1/ping",
                                "/api/v1/core/test-error",
                                "/tester.html",
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/metrics",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tenants").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tenants").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tenants/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tenants/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/technicians/workload")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN", "COORDINATOR", "AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/assets",
                                "/api/v1/consumptions",
                                "/api/v1/work-orders"
                        ).hasAnyRole("ADMIN", "COORDINATOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/incidents")
                        .hasAnyRole("ADMIN", "COORDINATOR", "CITIZEN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/zones")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/zones/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/zones/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/assets/**",
                                "/api/v1/consumptions/**",
                                "/api/v1/incidents/**",
                                "/api/v1/work-orders/**"
                        ).hasAnyRole("ADMIN", "COORDINATOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/assets/**",
                                "/api/v1/incidents/**",
                                "/api/v1/work-orders/**"
                        ).hasAnyRole("ADMIN", "COORDINATOR")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/zones",
                                "/api/v1/zones/**",
                                "/api/v1/assets",
                                "/api/v1/assets/**",
                                "/api/v1/consumptions/**",
                                "/api/v1/incidents",
                                "/api/v1/incidents/**"
                        ).hasAnyRole("ADMIN", "COORDINATOR", "AUDITOR", "CITIZEN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/work-orders", "/api/v1/work-orders/**")
                        .hasAnyRole("ADMIN", "COORDINATOR", "TECHNICIAN", "AUDITOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/evidence")
                        .hasAnyRole("ADMIN", "COORDINATOR", "TECHNICIAN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/evidence/**")
                        .hasAnyRole("ADMIN", "COORDINATOR", "TECHNICIAN", "AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/analytics/**")
                        .hasAnyRole("ADMIN", "COORDINATOR", "AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/ai/**")
                        .hasAnyRole("ADMIN", "COORDINATOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("""
                                    {
                                      "status": 401,
                                      "error": "UNAUTHORIZED",
                                      "message": "Authentication is required to access this resource"
                                    }
                                    """);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("""
                                    {
                                      "status": 403,
                                      "error": "FORBIDDEN",
                                      "message": "You do not have permission to access this resource"
                                    }
                                    """);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:5175",
                "http://127.0.0.1:5175"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PATCH",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "X-Correlation-Id"
        ));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
