package com.example.subsistemaSeguridad.usuariorol.dto;

import java.time.Instant;

public record UsuarioRolResponseDTO(
        Long id,
        int contadorUsuarioRol,
        Instant fechaAsignacionUsuarioRol,
        boolean activo,
        Long usuarioSistemaId,
        Long usuarioId,
        Long rolId
) {}
