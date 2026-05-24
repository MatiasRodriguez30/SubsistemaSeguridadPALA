package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.UsuarioAutenticadoDTO;
import com.example.subsistemaSeguridad.rolpermiso.RolPermiso;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuariorol.UsuarioRol;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    //intección de dependencias
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        //buscar usuario
        Usuario usuario = usuarioRepository
                .findByMailUsuarioAndFechaBajaUsuarioIsNull(request.mailUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña inválidos"));
        //validar contraseña
        boolean passwordCorrecta = passwordEncoder.matches(
                request.passwordUsuario(),
                usuario.getPasswordUsuario()
        );

        if (!passwordCorrecta) {
            throw new RuntimeException("Usuario o contraseña inválidos");
        }
        //obtener los roles "activos" del usuario
        List<String> roles = usuario.getRolesUsuario()
                .stream()
                .filter(usuarioRol -> !usuarioRol.estaDadoDeBaja())
                .map(UsuarioRol::getRol)
                .filter(rol -> rol != null && !rol.estaDadoDeBaja())
                .map(rol -> rol.getNombreRol())
                .distinct()
                .toList();
        //obtener los permisos "activos" del usuario
        List<String> permisos = usuario.getRolesUsuario()
                .stream()
                .filter(usuarioRol -> !usuarioRol.estaDadoDeBaja())
                .map(UsuarioRol::getRol)
                .filter(rol -> rol != null && !rol.estaDadoDeBaja())
                .flatMap(rol -> rol.getPermisosRol().stream())          //dejar flatmap, trust me bro
                .filter(rolPermiso -> !rolPermiso.estaDadoDeBaja())
                .map(RolPermiso::getPermiso)
                .filter(permiso -> permiso != null && !permiso.estaDadoDeBaja())
                .map(permiso -> permiso.getNombrePermiso())
                .distinct()
                .toList();
        //DTO para el token
        UsuarioAutenticadoDTO usuarioAutenticado = new UsuarioAutenticadoDTO(
                usuario.getId(),
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
                roles,
                permisos
        );
    }
}