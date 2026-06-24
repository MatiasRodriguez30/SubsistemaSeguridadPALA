package com.example.subsistemaSeguridad.auth.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class RolSolicitadoInvalidoException extends DomainException {

    public RolSolicitadoInvalidoException(String rolSolicitado, String systemKey) {
        super("El rol solicitado '" + rolSolicitado + "' no es valido o no se encuentra activo para el sistema " + systemKey);
    }
}
