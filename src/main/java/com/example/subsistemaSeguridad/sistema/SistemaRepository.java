package com.example.subsistemaSeguridad.sistema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SistemaRepository extends JpaRepository<Sistema, Long> {
    List<Sistema> findAllByFechaBajaSistemaIsNull();
    Optional<Sistema> findByIdAndFechaBajaSistemaIsNull(Long id);
}
