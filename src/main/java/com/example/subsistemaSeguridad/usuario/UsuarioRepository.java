package com.example.subsistemaSeguridad.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //agrego metodos para busqueda por email (auth)
    List<Usuario> findAllByFechaBajaUsuarioIsNull();

    Optional<Usuario> findByIdAndFechaBajaUsuarioIsNull(Long id);

    Optional<Usuario> findByMailUsuario(String mailUsuario);

    Optional<Usuario> findByMailUsuarioAndFechaBajaUsuarioIsNull(String mailUsuario);
}
