package com.example.subsistemaSeguridad.auth.dto;

import java.util.List;

public record LoginResponseDTO(
        String token,
        String tipo,
        Long usuarioId,
        String mailUsuario,
        List<String> roles,
        List<String> permisos
) {}