package com.example.subsistemaSeguridad.auth.dto;

public record RegisterResponseDTO(
        Long usuarioId,
        String mailUsuario,
        boolean verificationRequired,
        String message
) {}
