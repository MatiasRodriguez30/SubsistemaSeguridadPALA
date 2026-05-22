package com.example.subsistemaSeguridad.sistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class SistemaDadoDeBajaException extends DomainException {
    public SistemaDadoDeBajaException(Long id) {
        super("El sistema con id " + id + " está dado de baja y no puede ser modificado");
    }
}
