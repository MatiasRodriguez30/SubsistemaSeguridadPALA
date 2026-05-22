package com.example.subsistemaSeguridad.permiso.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class PermisoNotFoundException extends DomainException {
    public PermisoNotFoundException(Long id) {
        super("Permiso no encontrado con id: " + id);
    }
}
