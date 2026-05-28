## Ejecución local del proyecto

### Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java JDK.
- Docker Desktop.
- Git.
- IntelliJ IDEA o un IDE compatible.
- Maven Wrapper incluido en el proyecto.

> Nota: si Maven muestra un error relacionado con `JAVA_HOME`, verificar que la variable de entorno apunte correctamente al JDK instalado.

---

## Levantar servicios con Docker

El proyecto utiliza Docker Compose para levantar las bases de datos necesarias:

- PostgreSQL
- MongoDB
- Redis

Desde la carpeta `docker` ejecutar:

```bash
docker compose up -d
```

Verificar que los contenedores estén activos:

```bash
docker ps
```

Deben aparecer los servicios de PostgreSQL, MongoDB y Redis en ejecución.

Para apagar los servicios:

```bash
docker compose down
```

Para reiniciar completamente las bases de datos eliminando volúmenes:

```bash
docker compose down -v
```

> Usar `docker compose down -v` solo cuando se quiera borrar la información local y comenzar desde cero.

---

## Ejecutar el backend

Desde la carpeta `backend` ejecutar:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Si todo está correcto, la consola debe mostrar:

```text
Started BackendApplication
```

---

## Configuración de base de datos

El backend toma la configuración desde `application.properties`.

Por defecto, el proyecto está preparado para conectarse a los servicios levantados por Docker:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://127.0.0.1:5433/aguafutura}
spring.datasource.username=${DB_USERNAME:aguafutura}
spring.datasource.password=${DB_PASSWORD:aguafutura123}

spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/aguafutura}

spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
```

Esto permite ejecutar el proyecto localmente sin configurar variables adicionales, pero también permite sobrescribir valores mediante variables de entorno si se usa otro ambiente.

---

## Endpoints de verificación

Una vez levantado el backend, se pueden probar estos endpoints:

### Estado general del sistema

```text
http://localhost:8080/actuator/health
```

Debe mostrar los componentes principales en estado `UP`, incluyendo base de datos, MongoDB y Redis.

### Health propio del Core Platform

```text
http://localhost:8080/api/v1/health
```

### Ping con Correlation ID

```text
http://localhost:8080/api/v1/ping
```

Este endpoint permite comprobar que el backend responde y que el sistema genera un `correlationId` por request.

### Tenants demo

```text
http://localhost:8080/api/v1/tenants
```

### Swagger / OpenAPI

```text
http://localhost:8080/swagger-ui.html
```

Swagger permite visualizar y probar los endpoints disponibles del backend.

---

## Migraciones con Flyway

El proyecto utiliza Flyway para versionar la base de datos.

Migraciones actuales:

```text
V1__init_core.sql
V2__core_platform_base.sql
V3__seed_core_demo_tenants.sql
```

Regla importante:

```text
No modificar migraciones ya aplicadas.
Para nuevos cambios de base de datos, crear una nueva migración: V4, V5, V6, etc.
```

---

## Verificar tablas en PostgreSQL

Para entrar a PostgreSQL desde Docker:

```bash
docker exec -it aguafutura-postgres psql -U aguafutura -d aguafutura
```

Listar tablas:

```sql
\dt
```

Salir:

```sql
\q
```

---

## Ejecutar tests

Con los servicios de Docker levantados, desde la carpeta `backend` ejecutar:

```bash
./mvnw test
```

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```

---

## Compilar sin ejecutar

Desde la carpeta `backend`:

```bash
./mvnw clean compile
```

En Windows PowerShell:

```powershell
.\mvnw.cmd clean compile
```

Si todo está correcto, debe aparecer:

```text
BUILD SUCCESS
```

---

## Problemas comunes

### Error: `JAVA_HOME environment variable is not defined correctly`

Verificar que `JAVA_HOME` apunte al JDK instalado y que el directorio `bin` del JDK esté agregado al `PATH`.

En PowerShell se puede configurar temporalmente así:

```powershell
$env:JAVA_HOME="RUTA_DEL_JDK"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Luego verificar:

```powershell
java -version
echo $env:JAVA_HOME
```

### Error de conexión a PostgreSQL

Verificar que Docker esté corriendo y que el contenedor de PostgreSQL esté activo:

```bash
docker ps
```

También verificar que el puerto configurado en `application.properties` coincida con el puerto expuesto en `docker-compose.yml`.

### Swagger o endpoints redirigen a login

Revisar `SecurityConfig`, donde se permiten temporalmente endpoints públicos como:

```text
/api/v1/health
/api/v1/ping
/api/v1/tenants
/actuator/health
/swagger-ui.html
/api-docs
```

La autenticación real con JWT será implementada en el módulo IAM.
## AguaFutura AI - MVP local

Este repositorio contiene el MVP actual de AguaFutura AI: backend Spring Boot 3 / Java 21, frontend React/Vite y servicios locales con Docker.

Documentacion principal:

- Guia local: `docs/local-run.md`
- Guia de demo: `docs/demo-guide.md`
- Plan de pruebas manuales: `docs/manual-test-plan.md`
- Checklist de defensa: `docs/mvp-defense-checklist.md`
- Limitaciones conocidas: `docs/known-limitations.md`
