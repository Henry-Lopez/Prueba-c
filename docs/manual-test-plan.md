# AguaFutura AI - Plan de pruebas manuales MVP

## Alcance

Este plan valida el MVP actual sin exigir modulos futuros. Roles reales:

- `ADMIN`
- `COORDINATOR`
- `TECHNICIAN`
- `AUDITOR`

## Pruebas backend

| Caso | Pasos | Resultado esperado |
|---|---|---|
| Tests automatizados | Ejecutar `.\mvnw.cmd clean test` en `backend` | Build success, 17 tests sin fallos |
| Backend run | Ejecutar `.\mvnw.cmd spring-boot:run` | Backend en `http://localhost:8080` |
| Actuator | GET `/actuator/health` | `status: UP` |
| Swagger | GET `/swagger-ui.html` | HTTP 200 |
| Login | POST `/api/v1/auth/login` | Token Bearer |
| Perfil | GET `/api/v1/auth/me` con token | Usuario, roles y tenant |
| Registro publico | POST `/api/v1/auth/register` | HTTP 403 |

## Pruebas frontend

| Caso | Pasos | Resultado esperado |
|---|---|---|
| Build | Ejecutar `npm run build` en `aguafutura-frontend` | Build Vite exitoso |
| Dev server | Ejecutar `npm run dev -- --host 127.0.0.1 --port 5175` | Frontend disponible |
| Login | Usar usuario demo | Entra a layout principal |
| Dashboard | Entrar a `/` | KPIs reales, sin pantalla vacia |
| Activos | Entrar a `/assets` | Lista visible y boton `Ver detalle` |
| Detalle activo | Abrir `/assets/:assetId` | Datos reales, ubicacion textual, evidencia |
| Incidencias | Entrar a `/incidents` | Lista visible y boton `Ver detalle` |
| Detalle incidencia | Abrir `/incidents/:incidentId` | Datos reales, estado, severidad, activo |
| Ordenes | Entrar a `/work-orders` | Lista visible y boton `Ver detalle` |
| Detalle orden | Abrir `/work-orders/:workOrderId` | Datos reales, estado, prioridad, evidencia |
| Perfil | Entrar a `/profile` | Usuario, tenant y roles |

## Pruebas por rol

| Rol | Pruebas | Resultado esperado |
|---|---|---|
| ADMIN | Login, dashboard, activos, incidencias, ordenes, evidencia, IA | Flujo operativo completo |
| COORDINATOR | Login, dashboard, gestion operativa, evidencia | Gestion permitida; registro publico sigue bloqueado |
| TECHNICIAN | Login, menu limitado, ordenes, detalle de orden, evidencia | Sin acciones administrativas; escritura en activos/incidencias responde 403 |
| AUDITOR | Login, dashboard, lectura de activos/incidencias/ordenes, detalles | Sin botones de escritura; backend rechaza escritura con 403 |

## Pruebas de permisos backend

| Caso | Resultado esperado |
|---|---|
| `TECHNICIAN` POST `/api/v1/assets` | 403 |
| `TECHNICIAN` POST `/api/v1/incidents` | 403 |
| `TECHNICIAN` GET `/api/v1/work-orders` | 200 |
| `AUDITOR` POST `/api/v1/assets` | 403 |
| `AUDITOR` POST `/api/v1/evidence` | 403 |
| Descarga evidencia directa sin token | 401 |
| Descarga evidencia de otro tenant | 404 |

## Pruebas dashboard

| Metrica | Resultado esperado |
|---|---|
| Total activos | 4 |
| Total incidencias | 4 |
| Total ordenes | 4 |
| Consumo total | 660.8000 m3 |
| Breakdown incidencias | Por severidad y estado |
| Breakdown ordenes | Por estado |
| Alertas operativas | Basadas en estados reales |

## Pruebas evidencia

| Caso | Resultado esperado |
|---|---|
| Listar evidencia por referencia | 200 con lista o empty state |
| Descargar por evidenceId con token | 200 si existe |
| Descargar directo sin token | 401 |
| Descargar directo otro tenant | 404 |
| AUDITOR subir evidencia | 403/backend y boton oculto |

## Criterio de aceptacion

El MVP esta listo para demo si:

- Docker, backend y frontend levantan;
- tests backend y build frontend pasan;
- roles demo pueden iniciar sesion;
- dashboard muestra datos reales;
- detalles de activos, incidencias y ordenes funcionan;
- registro publico esta bloqueado;
- evidencia no queda publica;
- no hay botones muertos visibles en el flujo principal.
