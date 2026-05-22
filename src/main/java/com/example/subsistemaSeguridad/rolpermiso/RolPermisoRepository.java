package com.example.subsistemaSeguridad.rolpermiso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
    List<RolPermiso> findAllByFechaDesasignacionPermisoIsNull();
    Optional<RolPermiso> findByIdAndFechaDesasignacionPermisoIsNull(Long id);
}
