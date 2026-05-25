package com.example.subsistemaSeguridad.usuariosistema;

import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaCreateDTO;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioSistemaService {
    UsuarioSistema createUsuarioSistema(UsuarioSistemaCreateDTO dto);

    Optional<UsuarioSistema> getUsuarioSistemaById(Long id);

    List<UsuarioSistema> getAllUsuariosSistema();

    List<UsuarioSistema> getUsuariosSistemaByUsuarioId(Long usuarioId);

    List<UsuarioSistema> getUsuariosSistemaBySistemaId(Long sistemaId);

    UsuarioSistema updateUsuarioSistema(Long id, UsuarioSistemaUpdateDTO dto);

    void deleteUsuarioSistema(Long id);
}
