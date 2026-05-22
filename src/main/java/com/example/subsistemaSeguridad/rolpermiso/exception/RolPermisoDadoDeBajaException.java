package com.example.subsistemaSeguridad.rolpermiso.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class RolPermisoDadoDeBajaException extends DomainException {
    public RolPermisoDadoDeBajaException(Long id) {
        super("El RolPermiso con id " + id + " está dado de baja y no puede ser modificado");
    }
}
