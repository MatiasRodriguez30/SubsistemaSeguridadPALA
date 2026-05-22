package com.example.subsistemaSeguridad.rol.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class RolDadoDeBajaException extends DomainException {
    public RolDadoDeBajaException(Long id) {
        super("El rol con id " + id + " está dado de baja y no puede ser modificado");
    }
}
