package com.example.subsistemaSeguridad.usuariorol.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioRolDadoDeBajaException extends DomainException {
    public UsuarioRolDadoDeBajaException(Long id) {
        super("El UsuarioRol con id " + id + " está dado de baja y no puede ser modificado");
    }
}
