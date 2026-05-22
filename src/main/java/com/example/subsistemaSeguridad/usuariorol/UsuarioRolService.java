package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolCreateDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioRolService {
    UsuarioRol createUsuarioRol(UsuarioRolCreateDTO dto);
    Optional<UsuarioRol> getUsuarioRolById(Long id);
    List<UsuarioRol> getAllUsuarioRoles();
    void deleteUsuarioRol(Long id);
}
