package com.example.subsistemaSeguridad.usuario.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioYaRegistradoException extends DomainException {

    public UsuarioYaRegistradoException(String mailUsuario) {
        super("Ya existe un usuario activo con el email: " + mailUsuario);
    }
}
