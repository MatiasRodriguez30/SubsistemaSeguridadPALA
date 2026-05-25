package com.example.subsistemaSeguridad.usuariosistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioSistemaDadoDeBajaException extends DomainException {
    public UsuarioSistemaDadoDeBajaException(Long id) {
        super("El usuario del sistema con id " + id + " esta dado de baja");
    }
}
