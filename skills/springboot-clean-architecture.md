---
name: springboot-clean-architecture
description: >
  Actúa como desarrollador Senior en Java y Spring Boot. Aplica esta skill al generar,
  refactorizar o revisar cualquier código de backend en Spring Boot. Cubre arquitectura limpia,
  Domain-Driven Design, manejo de excepciones, DTOs, validaciones y buenas prácticas de Java moderno.
---

# Spring Boot — Clean Architecture & Domain-Driven Rules

Actúa como un desarrollador Senior en Java y Spring Boot. Al generar o refactorizar código, debes cumplir estrictamente las siguientes directrices de diseño.

---

## 1. Rich Domain Model (Tell, Don't Ask)

- Las entidades **NUNCA** deben ser anémicas (simples contenedores de getters y setters).
- La lógica de negocio, los cambios de estado y las validaciones internas pertenecen a la **Entidad**, no al Servicio.
- Oculta o elimina los setters públicos indiscriminados. Usa métodos semánticos (`actualizarDatos(dto)`, `darDeBaja()`, `activar()`).
- La entidad debe gestionar sus propios campos nulos utilizando métodos expresivos u `Optional` de manera interna.
- Exponer métodos de consulta de estado semánticos (`estaDadoDeBaja()`, `estaActivo()`) en lugar de dejar que capas externas comparen campos crudos.

**Ejemplo correcto:**
```java
// Dentro de la entidad Usuario
public void actualizarDatos(UsuarioUpdateDTO dto) {
    Optional.ofNullable(dto.getMailUsuario()).ifPresent(this::setMailUsuario);
    Optional.ofNullable(dto.getPasswordUsuario()).ifPresent(this::setPasswordUsuario);
}

public boolean estaDadoDeBaja() {
    return fechaBajaUsuario != null;
}

public void darDeBaja() {
    if (estaDadoDeBaja()) throw new UsuarioDadoDeBajaException(this.id);
    this.fechaBajaUsuario = LocalDateTime.now();
}
```

---

## 2. Thin Services (Servicios Delgados)

- Los `@Service` deben actuar **únicamente como orquestadores**.
- Flujo obligatorio del servicio:
  1. **Buscar** (Repository)
  2. **Validar** (lanzar excepción si falla)
  3. **Delegar** acción a la entidad
  4. **Guardar** (Repository)
- **Prohibido** hacer comprobaciones lógicas sobre el estado interno de la entidad directamente en el servicio.

| ❌ Evitar | ✅ Preferir |
|---|---|
| `if (usuario.getFechaBaja() != null)` | `if (usuario.estaDadoDeBaja())` |
| `usuario.setFechaBaja(LocalDateTime.now())` | `usuario.darDeBaja()` |
| Lógica de negocio en el service | Delegar a la entidad |

**Ejemplo correcto:**
```java
@Override
@Transactional
public Usuario updateUsuario(Long id, UsuarioUpdateDTO dto) {
    final Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new UsuarioNotFoundException(id));

    if (usuario.estaDadoDeBaja()) {
        throw new UsuarioDadoDeBajaException(id);
    }

    usuario.actualizarDatos(dto);

    return usuarioRepository.save(usuario);
}
```

---

## 3. Manejo de Excepciones

- **PROHIBIDO** usar excepciones genéricas (`RuntimeException`, `Exception`) para representar reglas de negocio.
- Crea excepciones personalizadas y expresivas.
- No uses bloques `try-catch` en controllers ni services para lógica de negocio. Deja que las excepciones fluyan hacia un `@RestControllerAdvice`.

**Estructura recomendada:**
```java
// Excepción base opcional para agrupar las de dominio
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
}

public class UsuarioNotFoundException extends DomainException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }
}

public class UsuarioDadoDeBajaException extends DomainException {
    public UsuarioDadoDeBajaException(Long id) {
        super("El usuario con id " + id + " está dado de baja y no puede ser modificado");
    }
}
```

**Handler global:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UsuarioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleDadoDeBaja(UsuarioDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        final String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(mensaje));
    }
}
```

---

## 4. DTOs como Frontera de la API

- Las entidades **nunca** se exponen directamente en los controllers (ni como request ni como response).
- Flujo obligatorio: `RequestDTO → Entidad → ResponseDTO`
- Las entidades no deben depender de DTOs más allá de los métodos de actualización delegados.
- Considerar **MapStruct** para el mapeo entre capas en proyectos medianos/grandes.

**Estructura de paquetes sugerida:**
```
dto/
  request/   → UsuarioCreateDTO, UsuarioUpdateDTO
  response/  → UsuarioResponseDTO
```

**Ejemplo con record (Java 16+):**
```java
// Request DTO
public record UsuarioUpdateDTO(
    @Email String mailUsuario,
    @Size(min = 8) String passwordUsuario
) {}

// Response DTO
public record UsuarioResponseDTO(
    Long id,
    String mailUsuario,
    boolean activo
) {}
```

---

## 5. Validación de Entrada

- Las validaciones de formato y presencia van en el **DTO** con anotaciones de Bean Validation (`@NotNull`, `@Email`, `@Size`, `@NotBlank`).
- Usar `@Valid` en el controller para activarlas automáticamente.
- **Nunca** replicar estas validaciones en el service o en la entidad.
- Las validaciones de **reglas de negocio** (ej: "no se puede dar de baja dos veces") pertenecen a la **entidad**.

```java
// Controller
@PutMapping("/{id}")
public ResponseEntity<UsuarioResponseDTO> update(
        @PathVariable final Long id,
        @RequestBody @Valid final UsuarioUpdateDTO dto) {
    final Usuario actualizado = usuarioService.updateUsuario(id, dto);
    return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
}
```

---

## 6. Repositorios

- Usar nombres de métodos expresivos que reflejen la intención: `findByMailAndActivoTrue()` en lugar de buscar todo y filtrar en el service.
- Queries complejas con `@Query` y JPQL, nunca lógica de filtrado en el service.
- **Prohibido** inyectar el `EntityManager` directamente en un service; si se necesita, encapsularlo en un repositorio o clase de infraestructura.

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMailUsuario(String mail);
    List<Usuario> findByFechaBajaUsuarioIsNull(); // activos
    boolean existsByMailUsuario(String mail);
}
```

---

## 7. Java Moderno y Clean Code

- Usar `Optional` de forma **funcional y declarativa** (`.map()`, `.orElseThrow()`, `.ifPresent()`). Nunca `.get()` sin verificar.
- Evitar bloques `if-else` anidados. Aplicar **Early Return**: retornar o lanzar excepción lo antes posible.
- Mantener el código **inmutable** siempre que sea posible usando variables `final`.
- Preferir **`record`** de Java para DTOs y objetos de valor inmutables.
- Usar **Streams** para transformaciones de colecciones en lugar de bucles imperativos.

```java
// ❌ Evitar
Optional<Usuario> opt = usuarioRepository.findById(id);
if (opt.isPresent()) {
    Usuario u = opt.get();
    // ...
}

// ✅ Preferir
final Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new UsuarioNotFoundException(id));
```

---

## 8. Estructura de Paquetes — Package by Feature

La organización es **por módulo/feature**: cada entidad agrupa todas sus capas en un mismo paquete. Esto mejora la cohesión, facilita encontrar todo lo relacionado a un dominio y escala bien a medida que el proyecto crece.

```
com.proyecto
├── usuario/
│   ├── Usuario.java                   ← Entidad rica
│   ├── UsuarioRepository.java
│   ├── UsuarioService.java            ← Interface
│   ├── UsuarioServiceImpl.java
│   ├── UsuarioController.java
│   ├── UsuarioMapper.java
│   ├── dto/
│   │   ├── UsuarioCreateDTO.java
│   │   ├── UsuarioUpdateDTO.java
│   │   └── UsuarioResponseDTO.java
│   └── exception/
│       ├── UsuarioNotFoundException.java
│       └── UsuarioDadoDeBajaException.java
│
├── producto/
│   ├── Producto.java
│   ├── ProductoRepository.java
│   ├── ProductoService.java
│   ├── ProductoServiceImpl.java
│   ├── ProductoController.java
│   ├── ProductoMapper.java
│   ├── dto/
│   │   ├── ProductoCreateDTO.java
│   │   └── ProductoResponseDTO.java
│   └── exception/
│       └── ProductoNotFoundException.java
│
└── shared/                            ← Código transversal a todos los módulos
    ├── exception/
    │   ├── DomainException.java       ← Excepción base abstracta
    │   └── GlobalExceptionHandler.java ← @RestControllerAdvice
    └── dto/
        └── ErrorResponse.java
```

**Reglas de esta estructura:**
- Cada módulo es **autocontenido**: su entidad, servicio, repositorio, DTOs y excepciones propias viven juntos.
- El paquete `shared/` es para código genuinamente transversal (handler global, excepción base, respuestas de error comunes). No usarlo como cajón de sastre.
- Las excepciones específicas de un módulo (`UsuarioNotFoundException`) van dentro del módulo, no en `shared/`.
- Si un módulo necesita datos de otro, lo hace a través del **Service**, nunca accediendo directamente al repositorio ajeno.

---

## Resumen de Reglas (Cheatsheet)

| Capa | Responsabilidad | Prohibido |
|---|---|---|
| **Entidad** | Lógica de negocio, cambios de estado, validaciones internas | Setters públicos indiscriminados, dependencias de infraestructura |
| **Service** | Orquestar: buscar → validar → delegar → guardar | Lógica de negocio, acceso a campos internos directamente |
| **Controller** | Recibir request, llamar service, devolver response | Lógica de negocio, manejo de excepciones con try-catch |
| **DTO** | Transportar datos, validaciones de formato | Lógica de negocio |
| **Repository** | Acceso a datos, queries expresivas | Lógica de negocio |
| **Exception** | Representar errores de dominio de forma expresiva | Usar `RuntimeException` genérico |
