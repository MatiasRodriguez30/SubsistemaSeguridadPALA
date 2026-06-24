package com.example.subsistemaSeguridad.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String mailUsuario,

        @NotBlank(message = "El codigo es obligatorio")
        @Pattern(regexp = "\\d{6}", message = "El codigo debe tener 6 digitos")
        String codigo,

        @NotBlank(message = "La nueva contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String nuevaPassword
) {}
