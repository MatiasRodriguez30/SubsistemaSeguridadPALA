package com.example.subsistemaSeguridad.shared.exception;

import com.example.subsistemaSeguridad.auth.exception.CorreoNoVerificadoException;
import com.example.subsistemaSeguridad.shared.dto.ErrorResponse;
import com.example.subsistemaSeguridad.auth.exception.CredencialesInvalidasException;
import com.example.subsistemaSeguridad.auth.exception.RolSolicitadoInvalidoException;
import com.example.subsistemaSeguridad.auth.exception.TokenInvalidoException;
import com.example.subsistemaSeguridad.codigoseguridad.exception.CodigoSeguridadExpiradoException;
import com.example.subsistemaSeguridad.codigoseguridad.exception.CodigoSeguridadInvalidoException;
import com.example.subsistemaSeguridad.mail.MailDeliveryException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaKeyNotFoundException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoNotFoundException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoNotFoundException;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolNotFoundException;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaNotFoundException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaYaRegistradoException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioYaRegistradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UsuarioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleDadoDeBaja(UsuarioDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioYaRegistrado(UsuarioYaRegistradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SistemaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSistemaNotFound(SistemaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SistemaDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleSistemaDadoDeBaja(SistemaDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SistemaKeyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSistemaKeyNotFound(SistemaKeyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRolNotFound(RolNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleRolDadoDeBaja(RolDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PermisoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePermisoNotFound(PermisoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PermisoDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handlePermisoDadoDeBaja(PermisoDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolPermisoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRolPermisoNotFound(RolPermisoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolPermisoDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleRolPermisoDadoDeBaja(RolPermisoDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioRolNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioRolNotFound(UsuarioRolNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioRolDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioRolDadoDeBaja(UsuarioRolDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioSistemaDadoDeBajaException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioSistemaDadoDeBaja(UsuarioSistemaDadoDeBajaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioSistemaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioSistemaNotFound(UsuarioSistemaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UsuarioSistemaYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioSistemaYaRegistrado(UsuarioSistemaYaRegistradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CorreoNoVerificadoException.class)
    public ResponseEntity<ErrorResponse> handleCorreoNoVerificado(CorreoNoVerificadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RolSolicitadoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleRolSolicitadoInvalido(RolSolicitadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CodigoSeguridadInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleCodigoSeguridadInvalido(CodigoSeguridadInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CodigoSeguridadExpiradoException.class)
    public ResponseEntity<ErrorResponse> handleCodigoSeguridadExpirado(CodigoSeguridadExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MailDeliveryException.class)
    public ResponseEntity<ErrorResponse> handleMailDelivery(MailDeliveryException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        final String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(mensaje));
    }
}
