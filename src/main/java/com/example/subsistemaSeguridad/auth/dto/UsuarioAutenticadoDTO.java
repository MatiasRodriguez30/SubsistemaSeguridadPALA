package com.example.subsistemaSeguridad.auth.dto;

import java.util.List;

public record UsuarioAutenticadoDTO(
        Long subjectId,
        String mailUsuario,
        List<String> roles,
        List<String> permisos
) {}
