package com.example.subsistemaSeguridad.permiso.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class PermisoDadoDeBajaException extends DomainException {
    public PermisoDadoDeBajaException(Long id) {
        super("El permiso con id " + id + " está dado de baja y no puede ser modificado");
    }
}
