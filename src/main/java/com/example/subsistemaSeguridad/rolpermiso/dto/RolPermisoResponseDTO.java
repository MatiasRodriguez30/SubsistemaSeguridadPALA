package com.example.subsistemaSeguridad.rolpermiso.dto;

import java.time.Instant;

public record RolPermisoResponseDTO(
    Long id,
    int contadorPermiso,
    Instant fechaAsignacionPermiso,
    boolean activo,
    Long rolId,
    Long permisoId
) {}
