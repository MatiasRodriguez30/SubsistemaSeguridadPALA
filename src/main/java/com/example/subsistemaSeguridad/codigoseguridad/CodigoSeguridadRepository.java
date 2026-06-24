package com.example.subsistemaSeguridad.codigoseguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoSeguridadRepository extends JpaRepository<CodigoSeguridad, Long> {

    Optional<CodigoSeguridad> findTopByUsuarioSistemaIdAndTipoAndFechaUsoIsNullOrderByFechaAltaDesc(
            Long usuarioSistemaId,
            TipoCodigoSeguridad tipo
    );

    Optional<CodigoSeguridad> findTopByUsuarioIdAndTipoAndFechaUsoIsNullOrderByFechaAltaDesc(
            Long usuarioId,
            TipoCodigoSeguridad tipo
    );
}
