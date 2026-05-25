package com.example.subsistemaSeguridad.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SistemaCreateDTO(
    @NotBlank(message = "El nombre del sistema es obligatorio")
    String nombreSistema,

    @NotNull(message = "El ID del usuario responsable es obligatorio")
    Long usuarioId
) {}
