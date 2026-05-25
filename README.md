# SEGURIDAD UTN - Backend (API REST)

Este es el backend del Subsistema de Seguridad y Gestión de Accesos (proyecto PALA - Universidad Tecnológica Nacional). Proporciona una API REST completa y robusta para gestionar la autenticación de usuarios y la administración centralizada de múltiples sistemas y sus respectivos roles y permisos.

## 🚀 Tecnologías Principales

*   **Lenguaje:** Java 17
*   **Framework:** Spring Boot 3.3.0
*   **Seguridad:** Spring Security + JWT (JSON Web Tokens via `jjwt`)
*   **Persistencia:** Spring Data JPA / Hibernate
*   **Base de Datos:** PostgreSQL (Producción) / H2 (Desarrollo y Testing)
*   **Documentación API:** Swagger / OpenAPI 3 (`springdoc-openapi`)
*   **Herramientas:** Lombok (para reducción de boilerplate), Gradle (Gestor de dependencias)

## 📦 Estructura de Entidades (Modelo de Dominio)

El sistema centraliza la gestión a través de las siguientes entidades principales:
*   **Usuario:** La cuenta general de una persona (identificada por su email).
*   **Sistema:** Las distintas aplicaciones o módulos que delegan su seguridad a este subsistema. Cada sistema posee una `keySistema` segura.
*   **Rol:** Los distintos perfiles de acceso definidos **por sistema** (ej. "Administrador", "Visualizador").
*   **UsuarioSistema:** La relación que indica que un usuario está habilitado para acceder a un sistema específico, con su propia contraseña local para dicho sistema si fuera necesario.
*   **UsuarioRol:** La asignación de roles a un `UsuarioSistema`.

## ⚙️ Requisitos Previos

*   Java Development Kit (JDK) 17 o superior.
*   PostgreSQL (Opcional para entorno de desarrollo local si se utiliza H2 en memoria).

## 🛠️ Instalación y Ejecución Local

Este proyecto utiliza el wrapper de Gradle (`gradlew`), por lo que no es necesario tener Gradle instalado globalmente en el sistema.

1.  **Clonar el repositorio y ubicarse en el directorio del backend:**
    ```bash
    cd subsistemaSeguridadBack
    ```

2.  **Construir el proyecto y descargar dependencias:**
    En Linux / macOS:
    ```bash
    ./gradlew build
    ```
    En Windows:
    ```bash
    gradlew.bat build
    ```

3.  **Ejecutar la aplicación (Development Server):**
    En Linux / macOS:
    ```bash
    ./gradlew bootRun
    ```
    En Windows:
    ```bash
    gradlew.bat bootRun
    ```

    *Nota: Alternativamente, en Windows puedes utilizar el script proporcionado `./start-dev.ps1`.*

La aplicación iniciará por defecto en el puerto `8080`.

## 📚 Documentación de la API (Swagger UI)

Una vez que el servidor backend esté en ejecución, puedes explorar y probar interactuar con los endpoints de la API REST a través de Swagger UI.
Abre tu navegador web y visita:

`http://localhost:8080/swagger-ui.html`

(La ruta de la documentación OpenAPI JSON se encuentra generalmente en `http://localhost:8080/v3/api-docs`).

## 🔐 Autenticación y Comunicación (JWT para Sistemas Externos)

Cuando un usuario externo (de un sistema asociado) se autentica exitosamente a través de la API, el subsistema de Seguridad UTN emitirá un **JSON Web Token (JWT)**. 

### Estructura del Payload del JWT
El token contendrá la información esencial del usuario, los roles y los permisos que tiene asignados dentro de ese sistema específico. El payload (decodificado) se verá de la siguiente forma:

```json
{
  "sub": "15",
  "mail": "usuario@ejemplo.com",
  "roles": ["Administrador", "Auditor"],
  "permisos": ["LEER_REPORTE", "EDITAR_USUARIO"],
  "iat": 1698765432,
  "exp": 1698851832
}
```

*   **`sub`**: El ID de la relación `usuarioSistemaId` (como String), que identifica unívocamente la cuenta de este usuario dentro del sistema específico.
*   **`mail`**: El correo electrónico del usuario.
*   **`roles`**: Arreglo con los nombres de los roles asignados a este usuario para el sistema.
*   **`permisos`**: Arreglo con los permisos granulares asociados a los roles que posee el usuario.
*   **`iat` / `exp`**: Fecha de emisión (Issued At) y expiración del token.

### ¿Cómo comunicarse usando el JWT?

El sistema asociado o el cliente frontend deberá guardar este token y enviarlo en **todas** las peticiones HTTP subsiguientes hacia los recursos protegidos de su propia API (o del propio Subsistema de Seguridad).

Se debe enviar mediante la cabecera (**Header**) HTTP `Authorization`, utilizando el esquema `Bearer`:

```http
GET /api/recurso-protegido HTTP/1.1
Host: api.tusistema.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOi...
```

**Validación:** Los sistemas asociados deben validar la firma de este JWT utilizando la clave secreta compartida (o mediante un endpoint de validación del Subsistema de Seguridad UTN) para autorizar las operaciones del usuario según sus roles descritos en el token.

---

## 💻 Ejemplo de Integración para Sistemas Externos

Para que un sistema externo (ej. "Portal de Alumnos" o "Sistema de Gestión") se comunique con el Subsistema de Seguridad UTN para autenticar o registrar a sus usuarios, debe utilizar su clave única (`keySistema`) enviándola a través de las cabeceras HTTP.

La idea central es que **cada sistema externo mantenga su `keySistema` como una variable de entorno oculta** (ej. `SYSTEM_KEY=PALA_01X9` en su archivo `.env`) y el usuario final nunca interactúe con ella ni la vea en el frontend. 

### Ventajas de usar la cabecera `X-System-Key`:
*   **Independencia del Frontend:** El cliente web/móvil no conoce ni maneja la llave del sistema.
*   **Separación de contexto:** No se mezclan datos personales del usuario (como email y contraseña) con credenciales de la aplicación.
*   **Reutilización:** Se puede utilizar fácilmente en interceptores HTTP para login, registro, refresco de tokens, etc.

### Flujo de peticiones (Ejemplos)

El sistema externo interceptará el request de login de su frontend y hará la petición al Subsistema de Seguridad adjuntando la cabecera `X-System-Key`.

#### 1. Registro de Usuario Externo
```http
POST /api/auth/registro-externo HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Content-Type: application/json

{
  "mailUsuario": "user@mail.com",
  "passwordUsuario": "123456"
}
```

#### 2. Login Externo
```http
POST /api/auth/login-externo HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Content-Type: application/json

{
  "mailUsuario": "user@mail.com",
  "passwordUsuario": "123456"
}
```

#### 3. Consultar perfil o permisos
```http
GET /api/auth/me HTTP/1.1
Host: localhost:8080
X-System-Key: PALA_01X9
Authorization: Bearer <El_JWT_del_Usuario>
```

Al recibir la cabecera `X-System-Key`, el Subsistema de Seguridad interpreta: *"Este usuario o solicitud pertenece al sistema identificado por PALA_01X9"*, creando y validando todos los datos dentro de ese contexto aislado.
