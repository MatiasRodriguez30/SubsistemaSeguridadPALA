package com.example.subsistemaSeguridad.mail;

public interface EmailService {

    void enviarCodigoVerificacion(String destinatario, String codigo);

    void enviarCodigoRecuperacionPassword(String destinatario, String codigo);
}
