package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.ForgotPasswordRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.ResetPasswordRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.UsuarioAutenticadoDTO;
import com.example.subsistemaSeguridad.auth.dto.VerifyEmailRequestDTO;
import com.example.subsistemaSeguridad.auth.exception.CorreoNoVerificadoException;
import com.example.subsistemaSeguridad.auth.exception.CredencialesInvalidasException;
import com.example.subsistemaSeguridad.auth.exception.RolSolicitadoInvalidoException;
import com.example.subsistemaSeguridad.codigoseguridad.CodigoSeguridadService;
import com.example.subsistemaSeguridad.codigoseguridad.TipoCodigoSeguridad;
import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.rolpermiso.RolPermiso;
import com.example.subsistemaSeguridad.shared.EmailNormalizer;
import com.example.subsistemaSeguridad.shared.dto.MessageResponse;
import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.sistema.SistemaRepository;
import com.example.subsistemaSeguridad.sistema.exception.SistemaKeyNotFoundException;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioYaRegistradoException;
import com.example.subsistemaSeguridad.usuariorol.UsuarioRol;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistemaRepository;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaYaRegistradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final SistemaRepository sistemaRepository;
    private final RolRepository rolRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CodigoSeguridadService codigoSeguridadService;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            UsuarioSistemaRepository usuarioSistemaRepository,
            SistemaRepository sistemaRepository,
            RolRepository rolRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            CodigoSeguridadService codigoSeguridadService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.sistemaRepository = sistemaRepository;
        this.rolRepository = rolRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.codigoSeguridadService = codigoSeguridadService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String normalizedMail = EmailNormalizer.normalize(request.mailUsuario());

        Usuario usuario = usuarioRepository
                .findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .orElseThrow(CredencialesInvalidasException::new);

        if (usuario.getPasswordUsuario() == null || !passwordEncoder.matches(
                request.passwordUsuario(),
                usuario.getPasswordUsuario()
        )) {
            throw new CredencialesInvalidasException();
        }

        if (!usuario.isCorreoVerificado()) {
            throw new CorreoNoVerificadoException();
        }

        return buildInternalLoginResponse(usuario);
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        String normalizedMail = EmailNormalizer.normalize(request.mailUsuario());

        usuarioRepository.findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .ifPresent(existing -> {
                    throw new UsuarioYaRegistradoException(existing.getMailUsuario());
                });

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .mailUsuario(normalizedMail)
                        .passwordUsuario(passwordEncoder.encode(request.passwordUsuario()))
                        .fechaAltaUsuario(Instant.now())
                        .build()
        );

        codigoSeguridadService.emitirCodigoVerificacionCorreo(usuario);

        return new RegisterResponseDTO(
                usuario.getId(),
                usuario.getMailUsuario(),
                true,
                "Registro iniciado. Enviamos un codigo al correo para verificar la cuenta."
        );
    }

    @Override
    @Transactional
    public LoginResponseDTO verifyEmail(VerifyEmailRequestDTO request) {
        Usuario usuario = buscarUsuarioActivo(request.mailUsuario());
        codigoSeguridadService.validarCodigo(
                usuario,
                TipoCodigoSeguridad.VERIFICACION_CORREO,
                request.codigo()
        );
        usuario.verificarCorreo();
        usuario = usuarioRepository.save(usuario);
        return buildInternalLoginResponse(usuario);
    }

    @Override
    @Transactional
    public MessageResponse resendVerification(ForgotPasswordRequestDTO request) {
        Usuario usuario = buscarUsuarioActivo(request.mailUsuario());
        if (usuario.isCorreoVerificado()) {
            return new MessageResponse("El correo ya se encuentra verificado.");
        }

        codigoSeguridadService.emitirCodigoVerificacionCorreo(usuario);
        return new MessageResponse("Enviamos un nuevo codigo de verificacion al correo.");
    }

    @Override
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequestDTO request) {
        usuarioRepository
                .findByMailUsuarioAndFechaBajaUsuarioIsNull(EmailNormalizer.normalize(request.mailUsuario()))
                .ifPresent(codigoSeguridadService::emitirCodigoRecuperacionPassword);

        return new MessageResponse("Si el correo existe, enviaremos un codigo para recuperar la contrasena.");
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequestDTO request) {
        Usuario usuario = buscarUsuarioActivo(request.mailUsuario());
        codigoSeguridadService.validarCodigo(
                usuario,
                TipoCodigoSeguridad.RECUPERACION_PASSWORD,
                request.codigo()
        );
        usuario.setPasswordUsuario(passwordEncoder.encode(request.nuevaPassword()));
        usuarioRepository.save(usuario);
        return new MessageResponse("La contrasena fue actualizada correctamente.");
    }

    @Override
    @Transactional
    public ExternalRegisterResponseDTO registerExternal(String systemKey, ExternalRegisterRequestDTO request) {
        String normalizedMail = EmailNormalizer.normalize(request.mailUsuario());
        String encodedPassword = passwordEncoder.encode(request.passwordUsuarioSistema());

        Sistema sistema = sistemaRepository.findByKeySistemaAndFechaBajaSistemaIsNull(systemKey)
                .orElseThrow(() -> new SistemaKeyNotFoundException(systemKey));

        Usuario usuario = usuarioRepository.findByMailUsuario(normalizedMail)
                .map(existing -> {
                    if (existing.estaDadoDeBaja()) {
                        existing.reactivar();
                    }
                    return existing;
                })
                .orElseGet(() -> Usuario.builder()
                        .mailUsuario(normalizedMail)
                        .fechaAltaUsuario(Instant.now())
                        .build());

        usuario = usuarioRepository.save(usuario);

        if (usuarioSistemaRepository
                .findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(usuario.getId(), sistema.getId())
                .isPresent()) {
            throw new UsuarioSistemaYaRegistradoException(usuario.getId(), sistema.getId());
        }

        final Rol rolSolicitado = rolRepository
                .findBySistemaIdAndNombreRolIgnoreCaseAndFechaBajaRolIsNull(sistema.getId(), request.rolSolicitado())
                .orElseThrow(() -> new RolSolicitadoInvalidoException(request.rolSolicitado(), systemKey));

        UsuarioSistema usuarioSistema = UsuarioSistema.builder()
                .usuario(usuario)
                .sistema(sistema)
                .passwordUsuarioSistema(encodedPassword)
                .fechaAltaUsuarioSistema(Instant.now())
                .build();

        asignarRolSolicitado(usuarioSistema, rolSolicitado);

        usuarioSistema = usuarioSistemaRepository.save(usuarioSistema);

        codigoSeguridadService.emitirCodigoVerificacionCorreo(usuarioSistema);

        return new ExternalRegisterResponseDTO(
                usuario.getId(),
                usuario.getMailUsuario(),
                sistema.getKeySistema(),
                true,
                "Registro iniciado. Enviamos un codigo al correo para verificar la cuenta."
        );
    }

    @Override
    public LoginResponseDTO loginExternal(String systemKey, LoginRequestDTO request) {
        String normalizedMail = EmailNormalizer.normalize(request.mailUsuario());

        UsuarioSistema usuarioSistema = usuarioSistemaRepository
                .findByUsuarioMailUsuarioAndSistemaKeySistemaAndFechaBajaUsuarioSistemaIsNull(
                        normalizedMail,
                        systemKey
                )
                .orElseThrow(CredencialesInvalidasException::new);

        if (usuarioSistema.estaDadoDeBaja()) {
            throw new UsuarioSistemaDadoDeBajaException(usuarioSistema.getId());
        }

        if (usuarioSistema.getPasswordUsuarioSistema() == null || !passwordEncoder.matches(
                request.passwordUsuario(),
                usuarioSistema.getPasswordUsuarioSistema()
        )) {
            throw new CredencialesInvalidasException();
        }

        if (!usuarioSistema.isCorreoVerificado()) {
            throw new CorreoNoVerificadoException();
        }

        return buildExternalLoginResponse(usuarioSistema);
    }

    @Override
    @Transactional
    public LoginResponseDTO verifyExternalEmail(String systemKey, VerifyEmailRequestDTO request) {
        UsuarioSistema usuarioSistema = buscarUsuarioSistemaActivo(systemKey, request.mailUsuario());
        codigoSeguridadService.validarCodigo(
                usuarioSistema,
                TipoCodigoSeguridad.VERIFICACION_CORREO,
                request.codigo()
        );
        usuarioSistema.verificarCorreo();
        usuarioSistema = usuarioSistemaRepository.save(usuarioSistema);
        return buildExternalLoginResponse(usuarioSistema);
    }

    @Override
    @Transactional
    public MessageResponse resendExternalVerification(String systemKey, ForgotPasswordRequestDTO request) {
        UsuarioSistema usuarioSistema = buscarUsuarioSistemaActivo(systemKey, request.mailUsuario());
        if (usuarioSistema.isCorreoVerificado()) {
            return new MessageResponse("El correo ya se encuentra verificado.");
        }

        codigoSeguridadService.emitirCodigoVerificacionCorreo(usuarioSistema);
        return new MessageResponse("Enviamos un nuevo codigo de verificacion al correo.");
    }

    @Override
    @Transactional
    public MessageResponse forgotExternalPassword(String systemKey, ForgotPasswordRequestDTO request) {
        usuarioSistemaRepository
                .findByUsuarioMailUsuarioAndSistemaKeySistemaAndFechaBajaUsuarioSistemaIsNull(
                        EmailNormalizer.normalize(request.mailUsuario()),
                        systemKey
                )
                .ifPresent(codigoSeguridadService::emitirCodigoRecuperacionPassword);

        return new MessageResponse("Si el correo existe, enviaremos un codigo para recuperar la contrasena.");
    }

    @Override
    @Transactional
    public MessageResponse resetExternalPassword(String systemKey, ResetPasswordRequestDTO request) {
        UsuarioSistema usuarioSistema = buscarUsuarioSistemaActivo(systemKey, request.mailUsuario());
        codigoSeguridadService.validarCodigo(
                usuarioSistema,
                TipoCodigoSeguridad.RECUPERACION_PASSWORD,
                request.codigo()
        );
        usuarioSistema.actualizarPasswordUsuarioSistema(passwordEncoder.encode(request.nuevaPassword()));
        usuarioSistemaRepository.save(usuarioSistema);
        return new MessageResponse("La contrasena fue actualizada correctamente.");
    }

    private void asignarRolSolicitado(UsuarioSistema usuarioSistema, Rol rol) {
        int contador = usuarioSistema.getRolesUsuarioSistema() != null
                ? usuarioSistema.getRolesUsuarioSistema().size() + 1
                : 1;

        UsuarioRol usuarioRol = UsuarioRol.builder()
                .usuarioSistema(usuarioSistema)
                .rol(rol)
                .contadorUsuarioRol(contador)
                .fechaAsignacionUsuarioRol(null)
                .build();

        usuarioSistema.getRolesUsuarioSistema().add(usuarioRol);
    }

    private LoginResponseDTO buildExternalLoginResponse(UsuarioSistema usuarioSistema) {
        List<String> roles = usuarioSistema.getRolesUsuarioSistema()
                .stream()
                .filter(usuarioRol -> !usuarioRol.estaDadoDeBaja())
                .map(UsuarioRol::getRol)
                .filter(rol -> rol != null && !rol.estaDadoDeBaja())
                .map(Rol::getNombreRol)
                .distinct()
                .toList();

        List<String> permisos = usuarioSistema.getRolesUsuarioSistema()
                .stream()
                .filter(usuarioRol -> !usuarioRol.estaDadoDeBaja())
                .map(UsuarioRol::getRol)
                .filter(rol -> rol != null && !rol.estaDadoDeBaja())
                .flatMap(rol -> rol.getPermisosRol().stream())
                .filter(rolPermiso -> !rolPermiso.estaDadoDeBaja())
                .map(RolPermiso::getPermiso)
                .filter(permiso -> permiso != null && !permiso.estaDadoDeBaja())
                .map(permiso -> permiso.getNombrePermiso())
                .distinct()
                .toList();

        Usuario usuario = usuarioSistema.getUsuario();
        String systemKey = usuarioSistema.getSistema().getKeySistema();

        UsuarioAutenticadoDTO usuarioAutenticado = new UsuarioAutenticadoDTO(
                usuarioSistema.getId(),
                usuario.getMailUsuario(),
                roles,
                permisos
        );

        String token = jwtService.generarToken(usuarioAutenticado);

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getMailUsuario(),
                systemKey,
                roles,
                permisos
        );
    }

    private LoginResponseDTO buildInternalLoginResponse(Usuario usuario) {
        UsuarioAutenticadoDTO usuarioAutenticado = new UsuarioAutenticadoDTO(
                usuario.getId(),
                usuario.getMailUsuario(),
                List.of(),
                List.of()
        );

        String token = jwtService.generarToken(usuarioAutenticado);

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getMailUsuario(),
                null,
                List.of(),
                List.of()
        );
    }

    private UsuarioSistema buscarUsuarioSistemaActivo(String systemKey, String mailUsuario) {
        String normalizedMail = EmailNormalizer.normalize(mailUsuario);
        return usuarioSistemaRepository
                .findByUsuarioMailUsuarioAndSistemaKeySistemaAndFechaBajaUsuarioSistemaIsNull(
                        normalizedMail,
                        systemKey
                )
                .orElseThrow(CredencialesInvalidasException::new);
    }

    private Usuario buscarUsuarioActivo(String mailUsuario) {
        String normalizedMail = EmailNormalizer.normalize(mailUsuario);
        return usuarioRepository
                .findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .orElseThrow(CredencialesInvalidasException::new);
    }
}
