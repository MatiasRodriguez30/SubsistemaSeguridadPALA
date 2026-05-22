package com.example.subsistemaSeguridad.shared.exception;

import com.example.subsistemaSeguridad.shared.dto.ErrorResponse;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoNotFoundException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoNotFoundException;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolNotFoundException;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolDadoDeBajaException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        final String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(mensaje));
    }
}
