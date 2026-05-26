# SEGURIDAD UTN - Backend (API REST)

Backend del Subsistema de Seguridad y Gestion de Accesos del proyecto PALA - Universidad Tecnologica Nacional. Centraliza autenticacion, registro, roles, permisos y administracion de multiples sistemas.

## Tecnologias principales

- Java 17
- Spring Boot 3.3.0
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL / H2
- Swagger / OpenAPI 3
- Gradle

## Modelo de dominio

- `Usuario`: cuenta general identificada por email.
- `Sistema`: aplicacion externa o modulo que delega su seguridad a este subsistema.
- `Rol`: perfil de acceso definido por sistema.
- `UsuarioSistema`: relacion entre usuario y sistema.
- `UsuarioRol`: roles asignados a un usuario dentro de un sistema.

## Requisitos previos

- JDK 17 o superior.
- PostgreSQL opcional para desarrollo local.

## Instalacion y ejecucion local

1. Ubicarse en el directorio del backend:

```bash
cd subsistemaSeguridadBack
```

2. Construir el proyecto:

```bash
gradlew.bat build
```

3. Ejecutar la aplicacion:

```bash
gradlew.bat bootRun
```

Alternativa en Windows:

```powershell
./start-dev.ps1
```

La API inicia por defecto en `http://localhost:8080`.

## Documentacion de la API

Con la aplicacion en ejecucion:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints de autenticacion

### Autenticacion interna del panel

Estos endpoints no requieren `X-System-Key` y pueden ser consumidos directamente por el frontend administrativo.

#### Registro

```http
POST /api/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "mailUsuario": "admin@ejemplo.com",
  "passwordUsuario": "123456"
}
```

#### Login

```http
POST /api/auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "mailUsuario": "admin@ejemplo.com",
  "passwordUsuario": "123456"
}
```

### Integracion con sistemas externos

Estos endpoints si requieren `X-System-Key`, porque identifican que sistema externo esta hablando con el subsistema.

#### Registro externo

```http
POST /api/auth/external/register HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Content-Type: application/json

{
  "mailUsuario": "user@mail.com",
  "passwordUsuario": "123456"
}
```

#### Login externo

```http
POST /api/auth/external/login HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Content-Type: application/json

{
  "mailUsuario": "user@mail.com",
  "passwordUsuario": "123456"
}
```

## JWT y contexto del sistema

Cuando el login o el registro finalizan correctamente, el backend devuelve un JWT. Ese token representa al usuario autenticado dentro de un sistema determinado.

### Payload recomendado del JWT

Para que el backend o un sistema consumidor puedan deducir el contexto sin reenviar `X-System-Key` en requests del navegador, conviene incluir datos del sistema dentro del token.

```json
{
  "sub": "15",
  "mail": "usuario@ejemplo.com",
  "sistemaId": 1,
  "sistema": "PALA",
  "roles": ["Administrador"],
  "permisos": ["LEER_REPORTE"],
  "iat": 1698765432,
  "exp": 1698851832
}
```

Referencia de claims:

- `sub`: identificador de la relacion del usuario dentro del sistema.
- `mail`: email del usuario autenticado.
- `sistemaId`: id interno del sistema.
- `sistema`: codigo o nombre corto del sistema.
- `roles`: roles asignados para ese sistema.
- `permisos`: permisos efectivos del usuario.

## Regla de uso de headers

- `X-System-Key`: solo para comunicacion backend externo -> Subsistema Seguridad.
- `Authorization: Bearer <JWT>`: para frontend, navegador o cualquier cliente que ya opera con un token emitido.
- No enviar `X-System-Key` en endpoints expuestos al navegador.

## Que no debe pasar

El navegador no debe ver nunca una cabecera como esta:

```http
X-System-Key: PALA_01X9
```

Si esa key llega al frontend, cualquier usuario podria inspeccionar la request desde las herramientas del navegador y reutilizarla. Por eso la `X-System-Key` debe quedar siempre del lado servidor.

## Flujo correcto de autenticacion externa

### Paso 1 - El usuario inicia sesion en el frontend

El frontend envia solo las credenciales del usuario al backend del sistema externo:

```json
{
  "mail": "matias@mail.com",
  "password": "123456"
}
```

### Paso 2 - El backend externo llama al Subsistema Seguridad

El backend externo agrega la `X-System-Key` y traduce el payload al formato esperado por este servicio:

```http
POST /api/auth/external/login HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Content-Type: application/json

{
  "mailUsuario": "matias@mail.com",
  "passwordUsuario": "123456"
}
```

Este paso es seguro porque:

- el usuario no ve la key;
- el frontend no conoce la key;
- solo el backend externo la maneja.

### Paso 3 - El Subsistema Seguridad devuelve el JWT

La respuesta incluye el token del usuario autenticado:

```json
{
  "token": "eyJhbGciOi..."
}
```

### Paso 4 - El frontend usa el JWT

Desde ese momento el frontend trabaja solo con el token:

```http
Authorization: Bearer eyJhbGciOi...
```

No necesita reenviar `X-System-Key`, porque el JWT ya identifica:

- quien es el usuario;
- a que sistema pertenece;
- que roles y permisos tiene.

## Ejemplo correcto para endpoints consumidos por frontend

Si el frontend consulta un endpoint protegido como perfil, sesion actual o permisos, debe enviar solo el JWT.

```http
GET /api/auth/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer <JWT>
```

En ese caso, el backend deduce el sistema y los permisos a partir de los claims del token, sin exponer `X-System-Key` al navegador.

## Ventajas de este enfoque

- El frontend no conoce ni maneja la llave privada del sistema externo.
- El contexto del sistema viaja dentro del JWT.
- Se evita mezclar credenciales de aplicacion con credenciales del usuario final.
- La integracion entre sistemas externos y subsistema queda mas coherente y segura.
