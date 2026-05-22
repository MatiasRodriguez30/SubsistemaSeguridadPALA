package com.example.subsistemaSeguridad.usuariorol.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioRolNotFoundException extends DomainException {
    public UsuarioRolNotFoundException(Long id) {
        super("UsuarioRol no encontrado con id: " + id);
    }
}
