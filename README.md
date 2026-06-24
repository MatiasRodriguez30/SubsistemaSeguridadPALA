# Subsistema de Seguridad PALA - Backend

Backend del Subsistema de Seguridad del proyecto PALA. Centraliza autenticacion, registro, verificacion de correo, recuperacion de contrasena, sistemas, usuarios, roles, permisos y asignaciones de acceso.

La idea principal es que los sistemas consumidores no implementen su propia seguridad. En su lugar, registran usuarios, roles y permisos en este servicio, y luego autentican contra esta API mediante JWT.

## Tecnologias

- Java 17
- Spring Boot 3.3
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- H2 para desarrollo local
- PostgreSQL para produccion, pensado para Supabase
- JavaMailSender para envio SMTP
- Swagger / OpenAPI
- Gradle

## Composicion del dominio

- `Usuario`: identidad global del usuario, identificada por email.
- `Sistema`: aplicacion cliente que delega su seguridad al subsistema.
- `UsuarioSistema`: cuenta de un usuario dentro de un sistema especifico.
- `Rol`: perfil de acceso perteneciente a un sistema.
- `Permiso`: accion o capacidad asignable.
- `UsuarioRol`: asignacion de roles a un usuario dentro de un sistema.
- `RolPermiso`: asignacion de permisos a un rol.
- `CodigoSeguridad`: codigos temporales para verificar correo y recuperar contrasena.

Hay dos flujos de autenticacion:

- Interno: usado por el panel administrativo del subsistema.
- Externo: usado por otros sistemas, mediante `X-System-Key`.

## Flujo interno

El panel propio del subsistema usa endpoints sin `X-System-Key`.

1. `POST /api/auth/register` crea el usuario interno y envia un codigo por mail.
2. `POST /api/auth/verify-email` valida el codigo y devuelve el JWT.
3. `POST /api/auth/login` inicia sesion solo si el correo ya fue verificado.
4. `POST /api/auth/password/forgot` envia codigo de recuperacion.
5. `POST /api/auth/password/reset` valida codigo y cambia la contrasena.

## Flujo externo

Los sistemas externos se identifican con `X-System-Key`. Esa key nunca debe estar en un navegador: debe vivir solo en el backend del sistema consumidor.

1. El backend externo llama a `POST /api/auth/external/register` con `X-System-Key`.
2. Seguridad crea o reutiliza el `Usuario`, crea el `UsuarioSistema`, asigna el rol solicitado y envia codigo.
3. El usuario verifica con `POST /api/auth/external/verify-email`.
4. El login externo usa `POST /api/auth/external/login`.
5. La recuperacion usa `/api/auth/external/password/forgot` y `/api/auth/external/password/reset`.

## Mail

El subsistema usa una cuenta emisora general. No se guarda una credencial Gmail por usuario, porque los usuarios finales no envian correo: el sistema envia correos transaccionales.

Usos actuales:

- Codigo de verificacion de correo.
- Codigo para recuperar contrasena.

La configuracion esta en `.env` local y en variables de entorno de Render. El archivo real `.env` no se versiona; usar `.env.example` como referencia.

```properties
MAIL_ENABLED=true
GMAIL_USERNAME=authseguridad.p.a.l.a@gmail.com
GMAIL_APP_PASSWORD=app_password_de_google
MAIL_FROM=authseguridad.p.a.l.a@gmail.com
MAIL_FROM_NAME=Subsistema Seguridad PALA
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
```

Para Gmail hay que activar verificacion en 2 pasos y generar una App Password. No se usa la contrasena normal de la cuenta.

Si `MAIL_ENABLED=false`, el backend no envia correo real y escribe el codigo en logs. Esto sirve para desarrollo local.

## Variables de entorno

Archivo local:

```text
.env
```

Plantilla versionable:

```text
.env.example
```

Variables principales:

```properties
PORT=8080
JWT_SECRET=poner_un_secreto_largo_de_32_caracteres_o_mas
JWT_EXPIRATION_MS=3600000

SPRING_DATASOURCE_URL=jdbc:postgresql://host:puerto/postgres?sslmode=require
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_DATASOURCE_USERNAME=usuario_supabase
SPRING_DATASOURCE_PASSWORD=password_supabase
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
H2_CONSOLE_ENABLED=false
```

Localmente, si no se cargan variables de Supabase, la app usa H2 en memoria.

## Supabase

Para produccion con Supabase se usa PostgreSQL.

En Supabase obtener:

- Host
- Puerto
- Database
- User
- Password

Luego formar una URL JDBC:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://host:puerto/postgres?sslmode=require
```

En Render conviene cargar todas las variables desde el panel de Environment Variables, no desde un archivo commiteado.

## Render

Configuracion sugerida:

- Build command: `./gradlew build`
- Start command: `./gradlew bootRun`
- Runtime: Java 17
- Environment variables: las mismas de `.env.example`

Render inyecta el puerto mediante `PORT`; `application.properties` ya usa:

```properties
server.port=${PORT:8080}
```

## Ejecucion local

```powershell
cd D:\PALASeminario\subsistemaSeguridadBack
.\gradlew.bat bootRun
```

API local:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Tests

```powershell
.\gradlew.bat test
```

## Endpoints principales

Internos:

- `POST /api/auth/register`
- `POST /api/auth/verify-email`
- `POST /api/auth/login`
- `POST /api/auth/resend-verification`
- `POST /api/auth/password/forgot`
- `POST /api/auth/password/reset`

Externos:

- `POST /api/auth/external/register`
- `POST /api/auth/external/verify-email`
- `POST /api/auth/external/login`
- `POST /api/auth/external/resend-verification`
- `POST /api/auth/external/password/forgot`
- `POST /api/auth/external/password/reset`

Administracion:

- `/api/usuarios`
- `/api/sistemas`
- `/api/usuario-sistemas`
- `/api/roles`
- `/api/permisos`
- `/api/usuario-roles`
- `/api/rol-permisos`

## Seguridad

- Las contrasenas se guardan hasheadas con BCrypt.
- Los codigos de verificacion se guardan hasheados.
- Los codigos expiran y tienen limite de intentos.
- Los JWT incluyen mail, roles y permisos.
- `X-System-Key` no debe enviarse desde el frontend.
- `.env` esta ignorado para evitar subir secretos.
