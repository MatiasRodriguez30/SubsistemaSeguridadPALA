package com.example.subsistemaSeguridad.usuariosistema;

import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.sistema.SistemaRepository;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaCreateDTO;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UsuarioSistemaMapper {

    private final UsuarioRepository usuarioRepository;
    private final SistemaRepository sistemaRepository;

    @Autowired
    public UsuarioSistemaMapper(UsuarioRepository usuarioRepository, SistemaRepository sistemaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sistemaRepository = sistemaRepository;
    }

    public UsuarioSistema toEntity(UsuarioSistemaCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(dto.usuarioId()));

        if (usuario.estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(usuario.getId());
        }

        Sistema sistema = sistemaRepository.findById(dto.sistemaId())
                .orElseThrow(() -> new SistemaNotFoundException(dto.sistemaId()));

        if (sistema.estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(sistema.getId());
        }

        return UsuarioSistema.builder()
                .usuario(usuario)
                .sistema(sistema)
                .fechaAltaUsuarioSistema(Instant.now())
                .build();
    }

    public UsuarioSistemaResponseDTO toResponseDTO(UsuarioSistema entity) {
        return new UsuarioSistemaResponseDTO(
                entity.getId(),
                entity.getUsuario() != null ? entity.getUsuario().getId() : null,
                entity.getSistema() != null ? entity.getSistema().getId() : null,
                entity.getFechaAltaUsuarioSistema(),
                !entity.estaDadoDeBaja()
        );
    }
}
