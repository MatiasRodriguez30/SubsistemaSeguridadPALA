package com.example.subsistemaSeguridad.permiso;

import com.example.subsistemaSeguridad.permiso.dto.PermisoCreateDTO;
import com.example.subsistemaSeguridad.permiso.dto.PermisoUpdateDTO;
import com.example.subsistemaSeguridad.permiso.exception.PermisoDadoDeBajaException;
import com.example.subsistemaSeguridad.permiso.exception.PermisoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;
    private final PermisoMapper permisoMapper;

    @Autowired
    public PermisoServiceImpl(PermisoRepository permisoRepository, PermisoMapper permisoMapper) {
        this.permisoRepository = permisoRepository;
        this.permisoMapper = permisoMapper;
    }

    @Override
    @Transactional
    public Permiso createPermiso(PermisoCreateDTO dto) {
        final Permiso permiso = permisoMapper.toEntity(dto);
        return permisoRepository.save(permiso);
    }

    @Override
    public Optional<Permiso> getPermisoById(Long id) {
        return permisoRepository.findByIdAndFechaBajaPermisoIsNull(id);
    }

    @Override
    public List<Permiso> getAllPermisos() {
        return permisoRepository.findAllByFechaBajaPermisoIsNull();
    }

    @Override
    @Transactional
    public Permiso updatePermiso(Long id, PermisoUpdateDTO dto) {
        final Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new PermisoNotFoundException(id));

        if (permiso.estaDadoDeBaja()) {
            throw new PermisoDadoDeBajaException(id);
        }

        permiso.actualizarDatos(dto);

        return permisoRepository.save(permiso);
    }

    @Override
    @Transactional
    public void deletePermiso(Long id) {
        final Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new PermisoNotFoundException(id));

        permiso.darDeBaja();
        permisoRepository.save(permiso);
    }
}
