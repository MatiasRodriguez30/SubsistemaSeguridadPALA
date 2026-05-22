package com.example.subsistemaSeguridad.permiso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    List<Permiso> findAllByFechaBajaPermisoIsNull();
    Optional<Permiso> findByIdAndFechaBajaPermisoIsNull(Long id);
}
