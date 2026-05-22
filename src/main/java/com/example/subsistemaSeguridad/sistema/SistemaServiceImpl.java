package com.example.subsistemaSeguridad.sistema;

import com.example.subsistemaSeguridad.sistema.dto.SistemaCreateDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SistemaServiceImpl implements SistemaService {

    private final SistemaRepository sistemaRepository;
    private final SistemaMapper sistemaMapper;

    @Autowired
    public SistemaServiceImpl(SistemaRepository sistemaRepository, SistemaMapper sistemaMapper) {
        this.sistemaRepository = sistemaRepository;
        this.sistemaMapper = sistemaMapper;
    }

    @Override
    @Transactional
    public Sistema createSistema(SistemaCreateDTO dto) {
        final Sistema sistema = sistemaMapper.toEntity(dto);
        return sistemaRepository.save(sistema);
    }

    @Override
    public Optional<Sistema> getSistemaById(Long id) {
        return sistemaRepository.findByIdAndFechaBajaSistemaIsNull(id);
    }

    @Override
    public List<Sistema> getAllSistemas() {
        return sistemaRepository.findAllByFechaBajaSistemaIsNull();
    }

    @Override
    @Transactional
    public Sistema updateSistema(Long id, SistemaUpdateDTO dto) {
        final Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new SistemaNotFoundException(id));

        if (sistema.estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(id);
        }

        sistema.actualizarDatos(dto);

        return sistemaRepository.save(sistema);
    }

    @Override
    @Transactional
    public void deleteSistema(Long id) {
        final Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new SistemaNotFoundException(id));

        sistema.darDeBaja();
        sistemaRepository.save(sistema);
    }
}
