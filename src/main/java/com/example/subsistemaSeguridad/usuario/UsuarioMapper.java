package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioCreateDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setMailUsuario(dto.mailUsuario());
        usuario.setPasswordUsuario(dto.passwordUsuario());
        usuario.setFechaAltaUsuario(Instant.now());
        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getMailUsuario(),
                entity.getFechaAltaUsuario(),
                !entity.estaDadoDeBaja()
        );
    }
}
