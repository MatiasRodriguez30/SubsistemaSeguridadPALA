package com.example.subsistemaSeguridad.usuario.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioNotFoundException extends DomainException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }
}
