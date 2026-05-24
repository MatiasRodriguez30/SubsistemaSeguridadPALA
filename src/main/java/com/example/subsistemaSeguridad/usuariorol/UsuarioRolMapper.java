package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolCreateDTO;
import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UsuarioRolMapper {

    private final RolRepository rolRepository;

    @Autowired
    public UsuarioRolMapper(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public UsuarioRol toEntity(UsuarioRolCreateDTO dto) {
        Rol rol = rolRepository.findById(dto.rolId())
                .orElseThrow(() -> new RolNotFoundException(dto.rolId()));

        if (rol.estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(rol.getId());
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setRol(rol);
        usuarioRol.setFechaAsignacionUsuarioRol(Instant.now());

        return usuarioRol;
    }

    public UsuarioRolResponseDTO toResponseDTO(UsuarioRol entity) {
        return new UsuarioRolResponseDTO(
                entity.getId(),
                entity.getContadorUsuarioRol(),
                entity.getFechaAsignacionUsuarioRol(),
                !entity.estaDadoDeBaja(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null,
                entity.getRol() != null ? entity.getRol().getId() : null
        );
    }
}