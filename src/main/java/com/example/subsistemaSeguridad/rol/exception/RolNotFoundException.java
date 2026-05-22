package com.example.subsistemaSeguridad.rol.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class RolNotFoundException extends DomainException {
    public RolNotFoundException(Long id) {
        super("Rol no encontrado con id: " + id);
    }
}
