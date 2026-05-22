package com.example.subsistemaSeguridad.rol;

import com.example.subsistemaSeguridad.rol.dto.RolCreateDTO;
import com.example.subsistemaSeguridad.rol.dto.RolResponseDTO;
import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.sistema.SistemaRepository;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RolMapper {

    private final SistemaRepository sistemaRepository;

    @Autowired
    public RolMapper(SistemaRepository sistemaRepository) {
        this.sistemaRepository = sistemaRepository;
    }

    public Rol toEntity(RolCreateDTO dto) {
        Rol rol = new Rol();
        rol.setNombreRol(dto.nombreRol());
        rol.setDescripcionRol(dto.descripcionRol());
        rol.setFechaAltaRol(Instant.now());
        
        Sistema sistema = sistemaRepository.findById(dto.sistemaId())
                .orElseThrow(() -> new SistemaNotFoundException(dto.sistemaId()));
                
        if (sistema.estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(sistema.getId());
        }
        
        rol.setSistema(sistema);
        
        return rol;
    }

    public RolResponseDTO toResponseDTO(Rol entity) {
        return new RolResponseDTO(
                entity.getId(),
                entity.getNombreRol(),
                entity.getDescripcionRol(),
                entity.getFechaAltaRol(),
                !entity.estaDadoDeBaja(),
                entity.getSistema() != null ? entity.getSistema().getId() : null
        );
    }
}
