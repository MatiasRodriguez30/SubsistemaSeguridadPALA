package com.example.subsistemaSeguridad.rol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RolCreateDTO(
    @NotBlank(message = "El nombre del rol es obligatorio")
    String nombreRol,
    
    String descripcionRol,
    
    @NotNull(message = "El ID del sistema es obligatorio")
    Long sistemaId
) {}
