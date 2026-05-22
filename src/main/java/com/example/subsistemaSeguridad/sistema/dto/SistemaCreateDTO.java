package com.example.subsistemaSeguridad.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SistemaCreateDTO(
    @NotBlank(message = "El nombre del sistema es obligatorio")
    String nombreSistema,
    
    @NotBlank(message = "La key del sistema es obligatoria")
    String keySistema,
    
    @NotNull(message = "El ID del usuario responsable es obligatorio")
    Long usuarioId
) {}
