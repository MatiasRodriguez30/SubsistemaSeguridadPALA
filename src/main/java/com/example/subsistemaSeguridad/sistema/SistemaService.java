package com.example.subsistemaSeguridad.sistema;

import com.example.subsistemaSeguridad.sistema.dto.SistemaCreateDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface SistemaService {
    Sistema createSistema(SistemaCreateDTO dto);
    Optional<Sistema> getSistemaById(Long id);
    List<Sistema> getAllSistemas();
    Sistema updateSistema(Long id, SistemaUpdateDTO dto);
    void deleteSistema(Long id);
}
