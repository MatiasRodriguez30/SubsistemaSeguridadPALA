package com.example.subsistemaSeguridad.rolpermiso.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class RolPermisoNotFoundException extends DomainException {
    public RolPermisoNotFoundException(Long id) {
        super("RolPermiso no encontrado con id: " + id);
    }
}
