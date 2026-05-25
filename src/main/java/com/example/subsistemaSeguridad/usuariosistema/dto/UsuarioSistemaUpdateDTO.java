package com.example.subsistemaSeguridad.usuariosistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioSistemaUpdateDTO(
        @NotBlank(message = "La contrasena del usuario del sistema es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String passwordUsuarioSistema
) {}
