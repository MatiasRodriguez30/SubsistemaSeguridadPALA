package com.example.subsistemaSeguridad.permiso.dto;

import java.time.Instant;

public record PermisoResponseDTO(
    Long id,
    String nombrePermiso,
    Instant fechaAltaPermiso,
    boolean activo,
    Long sistemaId
) {}
