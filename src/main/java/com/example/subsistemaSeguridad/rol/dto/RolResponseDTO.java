package com.example.subsistemaSeguridad.rol.dto;

import java.time.Instant;

public record RolResponseDTO(
    Long id,
    String nombreRol,
    String descripcionRol,
    Instant fechaAltaRol,
    boolean activo,
    Long sistemaId
) {}
