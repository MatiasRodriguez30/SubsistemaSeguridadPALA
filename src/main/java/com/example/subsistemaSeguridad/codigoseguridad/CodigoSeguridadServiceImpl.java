package com.example.subsistemaSeguridad.codigoseguridad;

import com.example.subsistemaSeguridad.codigoseguridad.exception.CodigoSeguridadExpiradoException;
import com.example.subsistemaSeguridad.codigoseguridad.exception.CodigoSeguridadInvalidoException;
import com.example.subsistemaSeguridad.mail.EmailService;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class CodigoSeguridadServiceImpl implements CodigoSeguridadService {

    private final CodigoSeguridadRepository codigoSeguridadRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long expirationMinutes;
    private final int maxAttempts;

    public CodigoSeguridadServiceImpl(
            CodigoSeguridadRepository codigoSeguridadRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.verification.expiration-minutes:15}") long expirationMinutes,
            @Value("${app.security.verification.max-attempts:5}") int maxAttempts
    ) {
        this.codigoSeguridadRepository = codigoSeguridadRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
        this.maxAttempts = maxAttempts;
    }

    @Override
    @Transactional
    public void emitirCodigoVerificacionCorreo(UsuarioSistema usuarioSistema) {
        String codigo = emitirCodigo(usuarioSistema, TipoCodigoSeguridad.VERIFICACION_CORREO);
        emailService.enviarCodigoVerificacion(usuarioSistema.getUsuario().getMailUsuario(), codigo);
    }

    @Override
    @Transactional
    public void emitirCodigoVerificacionCorreo(Usuario usuario) {
        String codigo = emitirCodigo(usuario, TipoCodigoSeguridad.VERIFICACION_CORREO);
        emailService.enviarCodigoVerificacion(usuario.getMailUsuario(), codigo);
    }

    @Override
    @Transactional
    public void emitirCodigoRecuperacionPassword(UsuarioSistema usuarioSistema) {
        String codigo = emitirCodigo(usuarioSistema, TipoCodigoSeguridad.RECUPERACION_PASSWORD);
        emailService.enviarCodigoRecuperacionPassword(usuarioSistema.getUsuario().getMailUsuario(), codigo);
    }

    @Override
    @Transactional
    public void emitirCodigoRecuperacionPassword(Usuario usuario) {
        String codigo = emitirCodigo(usuario, TipoCodigoSeguridad.RECUPERACION_PASSWORD);
        emailService.enviarCodigoRecuperacionPassword(usuario.getMailUsuario(), codigo);
    }

    @Override
    @Transactional
    public void validarCodigo(UsuarioSistema usuarioSistema, TipoCodigoSeguridad tipo, String codigo) {
        CodigoSeguridad codigoSeguridad = codigoSeguridadRepository
                .findTopByUsuarioSistemaIdAndTipoAndFechaUsoIsNullOrderByFechaAltaDesc(usuarioSistema.getId(), tipo)
                .orElseThrow(CodigoSeguridadInvalidoException::new);

        validarCodigo(codigoSeguridad, codigo);
    }

    @Override
    @Transactional
    public void validarCodigo(Usuario usuario, TipoCodigoSeguridad tipo, String codigo) {
        CodigoSeguridad codigoSeguridad = codigoSeguridadRepository
                .findTopByUsuarioIdAndTipoAndFechaUsoIsNullOrderByFechaAltaDesc(usuario.getId(), tipo)
                .orElseThrow(CodigoSeguridadInvalidoException::new);

        validarCodigo(codigoSeguridad, codigo);
    }

    private String emitirCodigo(UsuarioSistema usuarioSistema, TipoCodigoSeguridad tipo) {
        String codigo = generarCodigo();
        Instant ahora = Instant.now();

        CodigoSeguridad codigoSeguridad = CodigoSeguridad.builder()
                .usuarioSistema(usuarioSistema)
                .tipo(tipo)
                .codigoHash(passwordEncoder.encode(codigo))
                .fechaAlta(ahora)
                .fechaExpiracion(ahora.plus(expirationMinutes, ChronoUnit.MINUTES))
                .build();

        codigoSeguridadRepository.save(codigoSeguridad);
        return codigo;
    }

    private String emitirCodigo(Usuario usuario, TipoCodigoSeguridad tipo) {
        String codigo = generarCodigo();
        Instant ahora = Instant.now();

        CodigoSeguridad codigoSeguridad = CodigoSeguridad.builder()
                .usuario(usuario)
                .tipo(tipo)
                .codigoHash(passwordEncoder.encode(codigo))
                .fechaAlta(ahora)
                .fechaExpiracion(ahora.plus(expirationMinutes, ChronoUnit.MINUTES))
                .build();

        codigoSeguridadRepository.save(codigoSeguridad);
        return codigo;
    }

    private void validarCodigo(CodigoSeguridad codigoSeguridad, String codigo) {
        Instant ahora = Instant.now();
        if (codigoSeguridad.estaExpirado(ahora) || codigoSeguridad.getIntentos() >= maxAttempts) {
            codigoSeguridad.marcarUsado();
            codigoSeguridadRepository.save(codigoSeguridad);
            throw new CodigoSeguridadExpiradoException();
        }

        codigoSeguridad.registrarIntento();
        if (!passwordEncoder.matches(codigo, codigoSeguridad.getCodigoHash())) {
            codigoSeguridadRepository.save(codigoSeguridad);
            throw new CodigoSeguridadInvalidoException();
        }

        codigoSeguridad.marcarUsado();
        codigoSeguridadRepository.save(codigoSeguridad);
    }

    private String generarCodigo() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }
}
