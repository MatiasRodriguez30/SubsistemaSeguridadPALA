# Subsistema de Seguridad PALA - Backend

Este backend es un servicio centralizado de Gestión de Identidad y Accesos (IAM). Se encarga de manejar toda la seguridad, autenticación y autorización para múltiples sistemas o aplicaciones.

**¿De qué se trata y cómo funciona?**
La idea principal es que los sistemas consumidores (por ejemplo, una tienda online, un panel de control, etc.) **no implementen su propia seguridad ni gestionen contraseñas**. En su lugar, delegan esta responsabilidad a este Subsistema de Seguridad de la siguiente manera:
1. El sistema consumidor usa esta API para registrar a sus usuarios.
2. Esta API guarda al usuario, verifica su correo, y gestiona contraseñas (incluyendo recuperación).
3. Cuando un usuario intenta iniciar sesión en el sistema consumidor, dicho sistema envía las credenciales a esta API.
4. Si las credenciales son válidas, la API devuelve un token JWT con la información del usuario, incluyendo sus roles y permisos asignados para ese sistema en particular.

## Ejemplo de lo que devuelve (Respuesta exitosa)

Cuando el inicio de sesión es exitoso, la API devuelve un JSON que contiene el JWT (`token`), así como una lista de roles y permisos del usuario en el sistema que hizo la petición.

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWI...",
  "tipo": "Bearer",
  "usuarioId": 12,
  "mailUsuario": "usuario@ejemplo.com",
  "systemKey": "123e4567-e89b-12d3-a456-426614174000",
  "roles": [
    "ADMINISTRADOR"
  ],
  "permisos": [
    "CREAR_PRODUCTO",
    "ELIMINAR_USUARIO"
  ]
}
```

## Cómo conectarse (Para Sistemas Externos)

Para que un sistema consumidor pueda conectarse a este backend de seguridad, debe estar registrado en la base de datos de sistemas y obtener un **`X-System-Key`** (una clave UUID única). 

⚠️ **Importante**: Esta clave nunca debe enviarse desde el frontend (navegador web o app móvil) por motivos de seguridad. Debe configurarse como una variable de entorno en el backend del sistema consumidor, y es este backend quien se comunica con la API de seguridad.

### Ejemplo de conexión (Node.js / Express)

En el siguiente ejemplo, un sistema externo implementa su propio endpoint de `/login` para sus usuarios. Internamente, consume el endpoint de este backend de seguridad (`/api/auth/external/login`) adjuntando el `X-System-Key`.

```javascript
// Endpoint en el backend del sistema consumidor
app.post('/login', async (req, res) => {
    const { email, password } = req.body;

    try {
        // Hacemos la petición al Subsistema de Seguridad
        const respuesta = await fetch('http://localhost:8080/api/auth/external/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Identificamos el sistema enviando la clave secreta
                'X-System-Key': process.env.MI_SISTEMA_KEY 
            },
            body: JSON.stringify({ email, password })
        });

        const data = await respuesta.json();

        if (respuesta.ok) {
            // Usuario validado: Enviamos el token al frontend
            res.json({ 
                mensaje: "Login exitoso", 
                token: data.token,
                roles: data.roles
            });
        } else {
            // Credenciales incorrectas o usuario no verificado
            res.status(401).json({ error: data.message });
        }
    } catch (error) {
        res.status(500).json({ error: "Error conectando al sistema de seguridad" });
    }
});
```
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

El subsistema utiliza **Resend** para el envío de correos transaccionales. No se guardan credenciales de correo por usuario, ya que es el propio sistema quien emite los mensajes.

Usos actuales:

- Código de verificación de correo.
- Código para recuperar contraseña.

La configuración está en el archivo `.env` local y en las variables de entorno de Render. El archivo real `.env` no se versiona; usar `.env.example` como referencia.

```properties
MAIL_ENABLED=true
MAIL_PROVIDER=resend
RESEND_API_KEY=re_xxxxxxxxx
RESEND_FROM=Subsistema Seguridad PALA <onboarding@resend.dev>
RESEND_API_URL=https://api.resend.com/emails
```

Si `MAIL_ENABLED=false`, el backend no envía correo real y escribe el código en la consola (logs). Esto es útil para desarrollo local.

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
