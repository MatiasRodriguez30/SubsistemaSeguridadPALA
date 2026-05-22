package com.example.subsistemaSeguridad.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido") 
    String mailUsuario,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") 
    String passwordUsuario
) {}
