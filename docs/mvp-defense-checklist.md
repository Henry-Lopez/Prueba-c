# AguaFutura AI - Checklist de defensa MVP

## Antes de presentar

- [ ] Docker Desktop iniciado.
- [ ] `docker compose config` ejecuta sin errores.
- [ ] `docker compose up -d` ejecutado desde `docker`.
- [ ] `docker ps` muestra `aguafutura-postgres`, `aguafutura-mongodb`, `aguafutura-redis`.
- [ ] Backend iniciado en `http://localhost:8080`.
- [ ] `http://localhost:8080/actuator/health` responde `UP`.
- [ ] Swagger abre en `http://localhost:8080/swagger-ui.html`.
- [ ] Frontend iniciado en `http://localhost:5175`.
- [ ] `npm run build` paso.
- [ ] Login ADMIN probado.
- [ ] Login AUDITOR probado.
- [ ] Login TECHNICIAN probado.
- [ ] Dashboard muestra 4 activos, 4 incidencias, 4 ordenes y 660.8000 m3.
- [ ] Detalle de activo probado.
- [ ] Detalle de incidencia probado.
- [ ] Detalle de orden probado.
- [ ] Registro publico responde 403.
- [ ] Evidencia directa sin token responde 401.
- [ ] Credenciales demo disponibles.

## Comandos de arranque

```powershell
cd docker
docker compose config
docker compose up -d
docker ps
```

```powershell
cd ..\backend
$env:JAVA_HOME="RUTA_DEL_JDK_21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

```powershell
cd ..\aguafutura-frontend
npm install
npm run build
npm run dev -- --host 127.0.0.1 --port 5175
```

## Estado esperado

| Componente | Estado esperado |
|---|---|
| PostgreSQL | Contenedor arriba, puerto `5433` |
| MongoDB | Contenedor arriba, puerto `27017` |
| Redis | Contenedor arriba, puerto `6379` |
| Backend | `http://localhost:8080` |
| Frontend | `http://localhost:5175` |
| Actuator | `UP` |
| Tests backend | 17 tests OK |
| Build frontend | OK |

## Usuarios demo

| Rol | Email | Password |
|---|---|---|
| ADMIN | `admin@aguafutura.ai` | `Admin123!` |
| COORDINATOR | `coordinador@aguafutura.ai` | `Coord123!` |
| TECHNICIAN | `tecnico@aguafutura.ai` | `Tec123!` |
| AUDITOR | `auditor@aguafutura.ai` | `Auditor123!` |

## Flujo demo recomendado

1. Login como `ADMIN`.
2. Dashboard: explicar indicadores reales.
3. Activos: abrir detalle y mostrar ubicacion textual.
4. Incidencias: abrir detalle y mostrar estado/severidad.
5. Ordenes: abrir detalle y mostrar estado/prioridad/evidencia.
6. Evidencia: explicar que requiere autenticacion.
7. Logout/login como `AUDITOR`: demostrar solo lectura.
8. Logout/login como `TECHNICIAN`: demostrar menu limitado.
9. Mencionar limitaciones futuras sin venderlas como implementadas.

## Preguntas dificiles y respuestas

| Pregunta | Respuesta defendible |
|---|---|
| Hay mapa integrado? | No en este MVP. La ubicacion textual ya esta modelada y la UI deja claro que el mapa integrado es siguiente iteracion. |
| El tecnico solo ve sus ordenes? | Todavia no hay asignacion real por `userId`; se aplico restriccion simple por rol para no inventar un modelo incompleto. |
| Se pueden crear administradores desde registro? | No. El registro publico esta bloqueado con 403 y los usuarios demo vienen de seeds controlados. |
| La evidencia es publica? | No. La descarga requiere token y valida tenant/metadata. |
| Educacion hidrica esta lista? | No forma parte del MVP actual; queda como fase futura. |
| Auditoria esta completa? | Hay soporte parcial, pero la pantalla/API visible de auditoria no es parte central del MVP defendido. |

## No tocar antes de presentar

- Roles.
- Login.
- Seeds demo.
- Seguridad de registro/evidencia.
- Matriz de permisos.
- Puertos locales si ya funcionan.
