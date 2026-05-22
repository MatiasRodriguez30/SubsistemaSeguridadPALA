package com.example.subsistemaSeguridad.rolpermiso.dto;

import jakarta.validation.constraints.NotNull;

public record RolPermisoCreateDTO(
    @NotNull(message = "El ID del rol es obligatorio")
    Long rolId,
    
    @NotNull(message = "El ID del permiso es obligatorio")
    Long permisoId
) {}
