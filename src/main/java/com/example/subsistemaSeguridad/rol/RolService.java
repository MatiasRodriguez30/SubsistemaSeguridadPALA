package com.example.subsistemaSeguridad.rol;

import com.example.subsistemaSeguridad.rol.dto.RolCreateDTO;
import com.example.subsistemaSeguridad.rol.dto.RolUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface RolService {
    Rol createRol(RolCreateDTO dto);
    Optional<Rol> getRolById(Long id);
    List<Rol> getAllRoles();
    Rol updateRol(Long id, RolUpdateDTO dto);
    void deleteRol(Long id);
}
