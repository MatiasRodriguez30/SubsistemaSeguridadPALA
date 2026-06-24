package com.example.subsistemaSeguridad.auth.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class CorreoNoVerificadoException extends DomainException {
    public CorreoNoVerificadoException() {
        super("Debe verificar su correo antes de iniciar sesion.");
    }
}
