package com.example.subsistemaSeguridad.usuariosistema.dto;

import java.time.Instant;

public record UsuarioSistemaResponseDTO(
        Long id,
        Long usuarioId,
        Long sistemaId,
        Instant fechaAltaUsuarioSistema,
        boolean activo
) {}
