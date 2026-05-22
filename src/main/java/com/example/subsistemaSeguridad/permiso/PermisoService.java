package com.example.subsistemaSeguridad.permiso;

import com.example.subsistemaSeguridad.permiso.dto.PermisoCreateDTO;
import com.example.subsistemaSeguridad.permiso.dto.PermisoUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface PermisoService {
    Permiso createPermiso(PermisoCreateDTO dto);
    Optional<Permiso> getPermisoById(Long id);
    List<Permiso> getAllPermisos();
    Permiso updatePermiso(Long id, PermisoUpdateDTO dto);
    void deletePermiso(Long id);
}
