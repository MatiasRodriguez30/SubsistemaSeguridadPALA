package com.example.subsistemaSeguridad.codigoseguridad.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class CodigoSeguridadInvalidoException extends DomainException {
    public CodigoSeguridadInvalidoException() {
        super("El codigo ingresado no es valido.");
    }
}
