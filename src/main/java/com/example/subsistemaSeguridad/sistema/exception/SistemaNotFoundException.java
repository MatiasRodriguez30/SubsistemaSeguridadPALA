package com.example.subsistemaSeguridad.sistema.exception;

import com.example.subsistemaSeguridad.shared.exception.DomainException;

public class SistemaNotFoundException extends DomainException {
    public SistemaNotFoundException(Long id) {
        super("Sistema no encontrado con id: " + id);
    }
}
