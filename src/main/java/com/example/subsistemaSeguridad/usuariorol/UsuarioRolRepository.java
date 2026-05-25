package com.example.subsistemaSeguridad.usuariorol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findAllByFechaBajaRolUsuarioIsNull();
    Optional<UsuarioRol> findByIdAndFechaBajaRolUsuarioIsNull(Long id);
    Optional<UsuarioRol> findByUsuarioSistemaIdAndFechaBajaRolUsuarioIsNull(Long usuarioSistemaId);
}
