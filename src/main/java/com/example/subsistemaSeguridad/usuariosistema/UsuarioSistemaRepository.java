package com.example.subsistemaSeguridad.usuariosistema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByIdAndFechaBajaUsuarioSistemaIsNull(Long id);

    Optional<UsuarioSistema> findByUsuarioIdAndSistemaId(Long usuarioId, Long sistemaId);

    Optional<UsuarioSistema> findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(Long usuarioId, Long sistemaId);

    Optional<UsuarioSistema> findByUsuarioMailUsuarioAndSistemaKeySistemaAndFechaBajaUsuarioSistemaIsNull(
            String mailUsuario,
            String keySistema
    );

    List<UsuarioSistema> findAllByUsuarioIdAndFechaBajaUsuarioSistemaIsNull(Long usuarioId);

    List<UsuarioSistema> findAllBySistemaIdAndFechaBajaUsuarioSistemaIsNull(Long sistemaId);

    List<UsuarioSistema> findAllByFechaBajaUsuarioSistemaIsNull();
}
