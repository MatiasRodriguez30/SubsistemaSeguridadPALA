package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistemaRepository;
import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolCreateDTO;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolMapper usuarioRolMapper;

    @Autowired
    public UsuarioRolServiceImpl(
            UsuarioRolRepository usuarioRolRepository,
            UsuarioRepository usuarioRepository,
            UsuarioSistemaRepository usuarioSistemaRepository,
            RolRepository rolRepository,
            UsuarioRolMapper usuarioRolMapper
    ) {
        this.usuarioRolRepository = usuarioRolRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolMapper = usuarioRolMapper;
    }

    @Override
    @Transactional
    public UsuarioRol createUsuarioRol(UsuarioRolCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(dto.usuarioId()));

        if (usuario.estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(usuario.getId());
        }

        Rol rol = rolRepository.findById(dto.rolId())
                .orElseThrow(() -> new RolNotFoundException(dto.rolId()));

        if (rol.estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(rol.getId());
        }

        UsuarioSistema usuarioSistema = usuarioSistemaRepository
                .findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(usuario.getId(), rol.getSistema().getId())
                .orElseGet(() -> usuarioSistemaRepository.save(
                        UsuarioSistema.builder()
                                .usuario(usuario)
                                .sistema(rol.getSistema())
                                .fechaAltaUsuarioSistema(Instant.now())
                                .build()
                ));

        // Dar de baja el rol activo existente (un UsuarioSistema solo puede tener un rol activo)
        usuarioRolRepository
                .findByUsuarioSistemaIdAndFechaBajaRolUsuarioIsNull(usuarioSistema.getId())
                .ifPresent(rolActivo -> {
                    rolActivo.darDeBaja();
                    usuarioRolRepository.save(rolActivo);
                });

        UsuarioRol usuarioRol = usuarioRolMapper.toEntity(dto);
        usuarioRol.setUsuarioSistema(usuarioSistema);

        int contador = usuarioSistema.getRolesUsuarioSistema() != null
                ? usuarioSistema.getRolesUsuarioSistema().size() + 1
                : 1;

        usuarioRol.setContadorUsuarioRol(contador);
        usuarioSistema.getRolesUsuarioSistema().add(usuarioRol);

        usuarioSistemaRepository.save(usuarioSistema);

        return usuarioRol;
    }

    @Override
    public Optional<UsuarioRol> getUsuarioRolById(Long id) {
        return usuarioRolRepository.findByIdAndFechaBajaRolUsuarioIsNull(id);
    }

    @Override
    public List<UsuarioRol> getAllUsuarioRoles() {
        return usuarioRolRepository.findAllByFechaBajaRolUsuarioIsNull();
    }

    @Override
    @Transactional
    public void deleteUsuarioRol(Long id) {
        UsuarioRol usuarioRol = usuarioRolRepository.findById(id)
                .orElseThrow(() -> new UsuarioRolNotFoundException(id));

        usuarioRol.darDeBaja();
        usuarioRolRepository.save(usuarioRol);
    }
}
