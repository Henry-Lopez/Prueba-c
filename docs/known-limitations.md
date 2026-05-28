# AguaFutura AI - Limitaciones conocidas del MVP

Estas limitaciones estan documentadas para defensa. No son errores criticos del MVP actual; son alcance futuro.

| Limitacion | Estado actual | Como explicarla | Fase futura |
|---|---|---|---|
| Mapa integrado | No implementado | El MVP guarda y muestra ubicacion textual; la UI no promete mapa operativo y muestra placeholder profesional. | Integracion GIS/mapa |
| TECHNICIAN por `userId` | No filtra ordenes por asignacion real de usuario | El modelo actual no tiene una relacion completa usuario-orden; se aplico restriccion simple por rol para evitar inventar seguridad falsa. | Asignacion tecnica formal |
| Educacion hidrica | No implementada en MVP actual | Esta fuera del flujo operativo principal validado. | Modulo educativo |
| App movil | No implementada | El MVP se concentra en web administrativa y operativa. | Cliente movil |
| Cuadrillas completas | No implementado como modulo completo | Las ordenes existen; la gestion avanzada de cuadrillas queda fuera del MVP. | Modulo de cuadrillas |
| Auditoria visible | Existe soporte parcial, pero pantalla/API visible puede estar limitada | Las acciones criticas tienen base de auditoria parcial; una vista completa de auditoria es siguiente etapa. | Consola de auditoria |
| IA/fallback | Capacidad basica disponible si el endpoint responde | Se presenta como apoyo operacional basico, no como motor predictivo completo. | IA operacional avanzada |
| Problem Details estricto | Errores JSON consistentes, pero no todos usan RFC 7807 completo | Es suficiente para demo; se puede estandarizar mas adelante. | Hardening API |
| Paginacion amplia | Listados demo funcionan con pocos datos | Para volumen real debe agregarse paginacion/filtrado robusto. | Escalamiento de datos |

## Mensaje de defensa

"El MVP actual prioriza el flujo operativo defendible: login, datos demo reales, dashboard, activos, incidencias, ordenes, evidencia y permisos por rol. Las limitaciones documentadas son decisiones de alcance para no inflar el producto ni simular capacidades que todavia no estan completas."
