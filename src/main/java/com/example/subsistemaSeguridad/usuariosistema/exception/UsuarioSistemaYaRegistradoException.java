package com.example.subsistemaSeguridad.usuariosistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class UsuarioSistemaYaRegistradoException extends DomainException {
    public UsuarioSistemaYaRegistradoException(Long usuarioId, Long sistemaId) {
        super("El usuario con id " + usuarioId + " ya esta registrado en el sistema " + sistemaId);
    }
}
