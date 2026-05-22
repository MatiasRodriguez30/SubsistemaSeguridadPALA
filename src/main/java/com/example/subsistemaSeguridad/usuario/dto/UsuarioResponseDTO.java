package com.example.subsistemaSeguridad.usuario.dto;

import java.time.Instant;

public record UsuarioResponseDTO(
    Long id,
    String mailUsuario,
    Instant fechaAltaUsuario,
    boolean activo
) {}
