package com.example.subsistemaSeguridad.mail;

public class MailDeliveryException extends RuntimeException {

    private final String detail;

    public MailDeliveryException(String message, Throwable cause) {
        super(message, cause);
        this.detail = cause == null ? null : rootCauseMessage(cause);
    }

    public String getDetail() {
        return detail;
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }

        String message = current.getMessage();
        return current.getClass().getSimpleName() + (message == null || message.isBlank() ? "" : ": " + message);
    }
}
