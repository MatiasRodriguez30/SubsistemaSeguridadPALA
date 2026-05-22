package com.example.subsistemaSeguridad.permiso;

import com.example.subsistemaSeguridad.permiso.dto.PermisoCreateDTO;
import com.example.subsistemaSeguridad.permiso.dto.PermisoResponseDTO;
import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.sistema.SistemaRepository;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PermisoMapper {

    private final SistemaRepository sistemaRepository;

    @Autowired
    public PermisoMapper(SistemaRepository sistemaRepository) {
        this.sistemaRepository = sistemaRepository;
    }

    public Permiso toEntity(PermisoCreateDTO dto) {
        Permiso permiso = new Permiso();
        permiso.setNombrePermiso(dto.nombrePermiso());
        permiso.setFechaAltaPermiso(Instant.now());
        
        Sistema sistema = sistemaRepository.findById(dto.sistemaId())
                .orElseThrow(() -> new SistemaNotFoundException(dto.sistemaId()));
                
        if (sistema.estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(sistema.getId());
        }
        
        permiso.setSistema(sistema);
        
        return permiso;
    }

    public PermisoResponseDTO toResponseDTO(Permiso entity) {
        return new PermisoResponseDTO(
                entity.getId(),
                entity.getNombrePermiso(),
                entity.getFechaAltaPermiso(),
                !entity.estaDadoDeBaja(),
                entity.getSistema() != null ? entity.getSistema().getId() : null
        );
    }
}
