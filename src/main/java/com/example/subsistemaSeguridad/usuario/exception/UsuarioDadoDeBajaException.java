package com.example.subsistemaSeguridad.usuario.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioDadoDeBajaException extends DomainException {
    public UsuarioDadoDeBajaException(Long id) {
        super("El usuario con id " + id + " está dado de baja y no puede ser modificado");
    }
}
