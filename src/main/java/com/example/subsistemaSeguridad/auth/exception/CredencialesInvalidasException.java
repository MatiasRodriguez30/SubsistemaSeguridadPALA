package com.example.subsistemaSeguridad.auth.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class CredencialesInvalidasException extends DomainException {
    public CredencialesInvalidasException() {
        super("Usuario o contrasena invalidos");
    }
}
