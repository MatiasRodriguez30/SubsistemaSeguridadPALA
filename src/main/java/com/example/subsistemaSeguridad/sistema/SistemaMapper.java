package com.example.subsistemaSeguridad.sistema;

import com.example.subsistemaSeguridad.sistema.dto.SistemaCreateDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaResponseDTO;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SistemaMapper {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SistemaMapper(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Sistema toEntity(SistemaCreateDTO dto) {
        Sistema sistema = new Sistema();
        sistema.setNombreSistema(dto.nombreSistema());
        sistema.setFechaAltaSistema(Instant.now());
        
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(dto.usuarioId()));
        if (usuario.estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(usuario.getId());
        }
        sistema.setUsuario(usuario);
        
        return sistema;
    }

    public SistemaResponseDTO toResponseDTO(Sistema entity) {
        return new SistemaResponseDTO(
                entity.getId(),
                entity.getNombreSistema(),
                entity.getKeySistema(),
                entity.getFechaAltaSistema(),
                !entity.estaDadoDeBaja(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null
        );
    }
}
