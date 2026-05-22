package com.example.subsistemaSeguridad.permiso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PermisoCreateDTO(
    @NotBlank(message = "El nombre del permiso es obligatorio")
    String nombrePermiso,
    
    @NotNull(message = "El ID del sistema es obligatorio")
    Long sistemaId
) {}
