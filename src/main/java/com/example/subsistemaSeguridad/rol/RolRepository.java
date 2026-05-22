package com.example.subsistemaSeguridad.rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    List<Rol> findAllByFechaBajaRolIsNull();
    Optional<Rol> findByIdAndFechaBajaRolIsNull(Long id);
}
