package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolCreateDTO;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolMapper usuarioRolMapper;

    @Autowired
    public UsuarioRolServiceImpl(
            UsuarioRolRepository usuarioRolRepository,
            UsuarioRepository usuarioRepository,
            UsuarioRolMapper usuarioRolMapper
    ) {
        this.usuarioRolRepository = usuarioRolRepository;
        this.usuarioRepository = usuarioRepository;
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

        UsuarioRol usuarioRol = usuarioRolMapper.toEntity(dto);

        // IMPORTANTE:
        // Este es el lado dueño de la relación con Usuario.
        usuarioRol.setUsuario(usuario);

        int contador = usuario.getRolesUsuario() != null
                ? usuario.getRolesUsuario().size() + 1
                : 1;

        usuarioRol.setContadorUsuarioRol(contador);

        usuario.getRolesUsuario().add(usuarioRol);

        usuarioRepository.save(usuario);

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