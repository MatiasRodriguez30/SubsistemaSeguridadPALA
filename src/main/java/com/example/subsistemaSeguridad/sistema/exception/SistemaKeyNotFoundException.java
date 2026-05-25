package com.example.subsistemaSeguridad.sistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class SistemaKeyNotFoundException extends DomainException {
    public SistemaKeyNotFoundException(String keySistema) {
        super("No existe un sistema activo para la key " + keySistema);
    }
}
