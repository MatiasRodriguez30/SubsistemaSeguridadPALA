package com.example.subsistemaSeguridad.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
    @Email(message = "Debe ser un email válido") 
    String mailUsuario,
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") 
    String passwordUsuario
) {}
