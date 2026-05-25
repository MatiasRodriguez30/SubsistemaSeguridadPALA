package com.example.subsistemaSeguridad.usuariosistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioSistemaNotFoundException extends DomainException {
    public UsuarioSistemaNotFoundException(Long id) {
        super("UsuarioSistema no encontrado con id: " + id);
    }
}
