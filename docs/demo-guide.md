# AguaFutura AI - Guia de demo MVP

## Objetivo del MVP

AguaFutura AI muestra una consola web multi-tenant para gestion hidrica operativa. El MVP permite iniciar sesion, revisar indicadores reales del tenant demo, consultar activos hidricos, incidencias, ordenes de trabajo y evidencia, y demostrar permisos diferenciados por rol.

## Problema que resuelve

El sistema centraliza informacion que normalmente queda dispersa entre planillas, reportes manuales y conversaciones operativas:

- infraestructura hidrica registrada por zona;
- incidencias asociadas a activos;
- ordenes de trabajo relacionadas a incidencias;
- evidencia trazable por activo, incidencia u orden;
- indicadores operativos para priorizar decisiones.

## URLs locales

| Servicio | URL |
|---|---|
| Frontend | `http://localhost:5175` |
| Backend | `http://localhost:8080` |
| Actuator | `http://localhost:8080/actuator/health` |
| Swagger | `http://localhost:8080/swagger-ui.html` |

## Credenciales demo

Tenant demo: `Municipio Santa Cruz` (`11111111-1111-1111-1111-111111111111`).

| Rol | Email | Password | Enfoque de demo |
|---|---|---|---|
| ADMIN | `admin@aguafutura.ai` | `Admin123!` | Flujo completo operativo |
| COORDINATOR | `coordinador@aguafutura.ai` | `Coord123!` | Gestion operativa |
| TECHNICIAN | `tecnico@aguafutura.ai` | `Tec123!` | Ordenes y evidencia |
| AUDITOR | `auditor@aguafutura.ai` | `Auditor123!` | Solo lectura |

## Que mostrar primero

1. Login con `ADMIN`.
2. Dashboard: KPIs reales del backend.
3. Consumo total en `m3`.
4. Incidencias por severidad/estado.
5. Ordenes por estado.
6. Alertas operativas.

Este primer bloque prueba que el sistema no es una maqueta estatica: consume datos reales del backend y respeta tenant.

## Flujo demo paso a paso

1. Iniciar sesion como `ADMIN`.
2. Mostrar dashboard y explicar KPIs: 4 activos, 4 incidencias, 4 ordenes y 660.8000 m3.
3. Entrar a `Activos hidricos`.
4. Abrir el detalle de un activo.
5. Mostrar ubicacion textual y aclarar que el mapa integrado esta planificado para fase futura.
6. Entrar a `Incidencias`.
7. Abrir una incidencia y mostrar activo asociado, estado, severidad y evidencia.
8. Entrar a `Ordenes de trabajo`.
9. Abrir una orden y mostrar estado, prioridad, activo, incidencia relacionada y evidencia.
10. Entrar como `AUDITOR` y mostrar que puede leer, pero no ve acciones de escritura.
11. Entrar como `TECHNICIAN` y mostrar que ve ordenes/evidencia, sin opciones administrativas.
12. Mencionar que el registro publico esta bloqueado y que la descarga directa de evidencia requiere autenticacion.

## Momento fuerte

El momento fuerte de la demo es cambiar de `ADMIN` a `AUDITOR` y `TECHNICIAN`:

- `ADMIN` muestra gestion operativa completa.
- `AUDITOR` demuestra solo lectura.
- `TECHNICIAN` demuestra acceso acotado a ordenes y evidencia.

Esto defiende seguridad por rol y evita que la UI sea la unica barrera: el backend tambien responde `403` ante acciones no permitidas.

## Que evitar mostrar como si ya estuviera completo

- Mapa integrado de activos.
- App movil.
- Educacion hidrica.
- Modulo completo de cuadrillas.
- Asignacion real de ordenes por `userId` para tecnicos.
- Auditoria avanzada visible desde UI.

Estas capacidades son fase futura, no fallos bloqueantes del MVP actual.

## Guion corto

"AguaFutura AI es una plataforma multi-tenant para centralizar operacion hidrica. En este MVP mostramos el flujo principal: login seguro, dashboard con datos reales, activos hidricos, incidencias, ordenes de trabajo y evidencia. El dashboard resume el tenant demo con 4 activos, 4 incidencias, 4 ordenes y consumo consolidado en m3. Desde activos puedo abrir una ficha operativa, desde incidencias puedo ver el problema real asociado a un activo, y desde ordenes puedo revisar el trabajo planificado. La seguridad esta separada por roles reales: ADMIN y COORDINATOR operan, AUDITOR solo lee y TECHNICIAN queda limitado a ordenes/evidencia. El registro publico esta bloqueado y la evidencia no queda expuesta de forma anonima. Las capacidades como mapa integrado, app movil, educacion hidrica y cuadrillas completas quedan planificadas como fases futuras."
