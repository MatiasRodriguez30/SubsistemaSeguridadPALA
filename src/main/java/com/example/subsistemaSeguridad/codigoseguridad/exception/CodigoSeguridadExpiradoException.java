package com.example.subsistemaSeguridad.codigoseguridad.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class CodigoSeguridadExpiradoException extends DomainException {
    public CodigoSeguridadExpiradoException() {
        super("El codigo expiro. Solicita uno nuevo.");
    }
}
