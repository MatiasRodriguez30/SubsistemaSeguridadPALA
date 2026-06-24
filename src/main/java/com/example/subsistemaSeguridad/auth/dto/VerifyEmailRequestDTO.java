package com.example.subsistemaSeguridad.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String mailUsuario,

        @NotBlank(message = "El codigo es obligatorio")
        @Pattern(regexp = "\\d{6}", message = "El codigo debe tener 6 digitos")
        String codigo
) {}
