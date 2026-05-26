package com.example.subsistemaSeguridad.auth.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class TokenInvalidoException extends DomainException {

    public TokenInvalidoException() {
        super("El token de autenticacion es invalido o expiro.");
    }
}
