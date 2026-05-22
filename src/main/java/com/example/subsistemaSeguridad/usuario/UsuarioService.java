package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioCreateDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario createUsuario(UsuarioCreateDTO dto);
    Optional<Usuario> getUsuarioById(Long id);
    List<Usuario> getAllUsuarios();
    Usuario updateUsuario(Long id, UsuarioUpdateDTO dto);
    void deleteUsuario(Long id);
}
