package com.example.subsistemaSeguridad.auth.dto;

public record ExternalRegisterResponseDTO(
        Long usuarioId,
        String mailUsuario,
        String systemKey,
        boolean verificationRequired,
        String message
) {}
