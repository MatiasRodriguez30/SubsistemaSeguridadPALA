package com.example.subsistemaSeguridad.rolpermiso;

import com.example.subsistemaSeguridad.permiso.Permiso;
import com.example.subsistemaSeguridad.permiso.PermisoRepository;
import com.example.subsistemaSeguridad.permiso.exception.PermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoNotFoundException;
import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoCreateDTO;
import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RolPermisoMapper {

    private final PermisoRepository permisoRepository;

    @Autowired
    public RolPermisoMapper(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public RolPermiso toEntity(RolPermisoCreateDTO dto) {
        Permiso permiso = permisoRepository.findById(dto.permisoId())
                .orElseThrow(() -> new PermisoNotFoundException(dto.permisoId()));

        if (permiso.estaDadoDeBaja()) {
            throw new PermisoDadoDeBajaException(permiso.getId());
        }

        RolPermiso rolPermiso = new RolPermiso();
        rolPermiso.setPermiso(permiso);
        rolPermiso.setFechaAsignacionPermiso(Instant.now());

        return rolPermiso;
    }

    public RolPermisoResponseDTO toResponseDTO(RolPermiso entity) {
        return new RolPermisoResponseDTO(
                entity.getId(),
                entity.getContadorPermiso(),
                entity.getFechaAsignacionPermiso(),
                !entity.estaDadoDeBaja(),
                entity.getRol() != null ? entity.getRol().getId() : null,
                entity.getPermiso() != null ? entity.getPermiso().getId() : null
        );
    }
}