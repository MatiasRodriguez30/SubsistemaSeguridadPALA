package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioCreateDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioResponseDTO;
import com.example.subsistemaSeguridad.shared.EmailNormalizer;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setMailUsuario(EmailNormalizer.normalize(dto.mailUsuario()));

        // No seteamos la contraseña acá porque debe guardarse encriptada (UsuarioServiceImpl)
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
