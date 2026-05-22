package com.example.subsistemaSeguridad.sistema.dto;

import java.time.Instant;

public record SistemaResponseDTO(
    Long id,
    String nombreSistema,
    String keySistema,
    Instant fechaAltaSistema,
    boolean activo,
    Long usuarioId
) {}
