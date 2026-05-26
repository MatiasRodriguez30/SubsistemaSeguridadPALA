package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.UsuarioAutenticadoDTO;
import com.example.subsistemaSeguridad.auth.exception.CredencialesInvalidasException;
import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.rolpermiso.RolPermiso;
import com.example.subsistemaSeguridad.shared.EmailNormalizer;
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

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            UsuarioSistemaRepository usuarioSistemaRepository,
            SistemaRepository sistemaRepository,
            RolRepository rolRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.sistemaRepository = sistemaRepository;
        this.rolRepository = rolRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
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

    @Override
    @Transactional
    public LoginResponseDTO registerExternal(String systemKey, ExternalRegisterRequestDTO request) {
        String normalizedMail = EmailNormalizer.normalize(request.mailUsuario());

        Sistema sistema = sistemaRepository.findByKeySistemaAndFechaBajaSistemaIsNull(systemKey)
                .orElseThrow(() -> new SistemaKeyNotFoundException(systemKey));

        Usuario usuario = usuarioRepository.findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.builder()
                                .mailUsuario(normalizedMail)
                                .fechaAltaUsuario(Instant.now())
                                .build()
                ));

        if (usuario.estaDadoDeBaja()) {
            throw new com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException(usuario.getId());
        }

        usuarioSistemaRepository
                .findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(usuario.getId(), sistema.getId())
                .ifPresent(existing -> {
                    throw new UsuarioSistemaYaRegistradoException(usuario.getId(), sistema.getId());
                });

        UsuarioSistema usuarioSistema = UsuarioSistema.builder()
                .usuario(usuario)
                .sistema(sistema)
                .passwordUsuarioSistema(passwordEncoder.encode(request.passwordUsuarioSistema()))
                .fechaAltaUsuarioSistema(Instant.now())
                .build();

        final UsuarioSistema usuarioSistemaParaRol = usuarioSistema;
        rolRepository.findBySistemaIdAndEsPredeterminadaTrueAndFechaBajaRolIsNull(sistema.getId())
                .ifPresent(rol -> asignarRolPredeterminado(usuarioSistemaParaRol, rol));

        usuarioSistema = usuarioSistemaRepository.save(usuarioSistema);

        return buildExternalLoginResponse(usuarioSistema);
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

        return buildExternalLoginResponse(usuarioSistema);
    }

    private void asignarRolPredeterminado(UsuarioSistema usuarioSistema, Rol rol) {
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
}
