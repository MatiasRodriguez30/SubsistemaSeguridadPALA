package com.example.subsistemaSeguridad.rolpermiso;

import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoCreateDTO;

import java.util.List;
import java.util.Optional;

public interface RolPermisoService {
    RolPermiso createRolPermiso(RolPermisoCreateDTO dto);
    Optional<RolPermiso> getRolPermisoById(Long id);
    List<RolPermiso> getAllRolPermisos();
    void deleteRolPermiso(Long id);
}
