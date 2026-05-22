package com.example.subsistemaSeguridad.usuariorol.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioRolCreateDTO(
    @NotNull(message = "El ID del usuario es obligatorio")
    Long usuarioId,
    
    @NotNull(message = "El ID del rol es obligatorio")
    Long rolId
) {}
