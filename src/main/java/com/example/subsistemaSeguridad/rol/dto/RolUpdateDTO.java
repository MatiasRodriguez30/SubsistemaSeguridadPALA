package com.example.subsistemaSeguridad.rol.dto;

public record RolUpdateDTO(
        String nombreRol,
        String descripcionRol,
        Boolean esPredeterminada
) {}
