package com.example.subsistemaSeguridad.usuariosistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioSistemaCreateDTO(
        @NotNull(message = "El ID del usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "El ID del sistema es obligatorio")
        Long sistemaId,

        @NotBlank(message = "La contrasena del usuario del sistema es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String passwordUsuarioSistema
) {}
