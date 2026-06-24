package com.example.subsistemaSeguridad.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email valido")
        String mailUsuario
) {}
