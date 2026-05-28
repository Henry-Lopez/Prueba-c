# AguaFutura AI - Ejecucion local en Windows

Esta guia cubre solo el arranque local del MVP actual con Docker, backend Spring Boot y frontend Vite.

Documentos relacionados:

- Demo: `docs/demo-guide.md`
- Plan de pruebas manuales: `docs/manual-test-plan.md`
- Checklist de defensa: `docs/mvp-defense-checklist.md`
- Limitaciones conocidas: `docs/known-limitations.md`

## Requisitos

- Windows PowerShell.
- Docker Desktop iniciado.
- JDK 21 instalado.
- Node.js y npm instalados.

## Configurar JDK 21 en la sesion actual

Si `java -version` no funciona o Maven indica que `JAVA_HOME` esta mal configurado, define el JDK 21 en la sesion de PowerShell:

```powershell
$env:JAVA_HOME="RUTA_DEL_JDK_21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

El resultado esperado debe mostrar Java 21. No hardcodees esta ruta en el proyecto; ajustala segun la instalacion local del equipo.

## Verificar estado del repositorio

```powershell
git status
git branch --show-current
```

Si Git muestra `dubious ownership` en un entorno sandbox, usa una consulta puntual sin cambiar archivos:

```powershell
git -c safe.directory=C:/Users/irisc/IdeaProjects/aguafutura-platform status
git -c safe.directory=C:/Users/irisc/IdeaProjects/aguafutura-platform branch --show-current
```

## Levantar servicios Docker

Desde la raiz del repositorio:

```powershell
cd docker
docker compose config
docker compose up -d
docker ps
```

Servicios esperados:

- PostgreSQL: `127.0.0.1:5433`, base `aguafutura`, usuario `aguafutura`, password `aguafutura123`.
- MongoDB: `mongodb://localhost:27017/aguafutura`.
- Redis: `localhost:6379`.

## Backend

Desde `docker`, vuelve al backend:

```powershell
cd ..\backend
.\mvnw.cmd -v
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Endpoints de verificacion cuando el backend este levantado:

```powershell
Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/v1/health
Invoke-WebRequest -UseBasicParsing http://localhost:8080/actuator/health
Invoke-WebRequest -UseBasicParsing http://localhost:8080/swagger-ui.html
Invoke-WebRequest -UseBasicParsing http://localhost:8080/api-docs
```

El backend usa `backend/src/main/resources/application.properties`, que ya apunta a los puertos locales publicados por `docker/docker-compose.yml`.

## Credenciales demo

Tenant demo: `Municipio Santa Cruz` (`11111111-1111-1111-1111-111111111111`).

| Rol | Email | Password |
|---|---|---|
| ADMIN | `admin@aguafutura.ai` | `Admin123!` |
| COORDINATOR | `coordinador@aguafutura.ai` | `Coord123!` |
| TECHNICIAN | `tecnico@aguafutura.ai` | `Tec123!` |
| AUDITOR | `auditor@aguafutura.ai` | `Auditor123!` |

## Registro publico

El MVP local usa usuarios seed para los cuatro roles reales. El endpoint publico `POST /api/v1/auth/register` queda deshabilitado para demo y responde `403`, porque permitir seleccion libre de `ADMIN`, `COORDINATOR`, `TECHNICIAN` o `AUDITOR` desde una pantalla publica no es defendible para presentacion.

## Frontend

Desde `backend`, vuelve al frontend:

```powershell
cd ..\aguafutura-frontend
npm install
npm run build
npm run dev
```

Por defecto, el frontend usa:

```text
VITE_API_BASE_URL=http://localhost:8080
```

Si necesitas cambiarlo solo para una sesion de PowerShell:

```powershell
$env:VITE_API_BASE_URL="http://localhost:8080"
npm run dev
```

## Notas de alcance

- No se agregan roles nuevos.
- No se agregan modulos nuevos.
- Los usuarios demo se gestionan por seeds Flyway.
- La UI oculta acciones segun los roles reales y el backend mantiene la validacion por rol.
