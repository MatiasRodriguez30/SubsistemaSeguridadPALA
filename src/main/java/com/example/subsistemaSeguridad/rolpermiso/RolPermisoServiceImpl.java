package com.example.subsistemaSeguridad.rolpermiso;

import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoCreateDTO;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolPermisoServiceImpl implements RolPermisoService {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolRepository rolRepository;
    private final RolPermisoMapper rolPermisoMapper;

    @Autowired
    public RolPermisoServiceImpl(
            RolPermisoRepository rolPermisoRepository,
            RolRepository rolRepository,
            RolPermisoMapper rolPermisoMapper
    ) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
        this.rolPermisoMapper = rolPermisoMapper;
    }

    @Override
    @Transactional
    public RolPermiso createRolPermiso(RolPermisoCreateDTO dto) {

        Rol rol = rolRepository.findById(dto.rolId())
                .orElseThrow(() -> new RolNotFoundException(dto.rolId()));

        if (rol.estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(rol.getId());
        }

        RolPermiso rolPermiso = rolPermisoMapper.toEntity(dto);

        // IMPORTANTE:
        // Este es el lado dueño de la relación con Rol.
        rolPermiso.setRol(rol);

        int contador = rol.getPermisosRol() != null
                ? rol.getPermisosRol().size() + 1
                : 1;

        rolPermiso.setContadorPermiso(contador);

        rol.getPermisosRol().add(rolPermiso);

        rolRepository.save(rol);

        return rolPermiso;
    }

    @Override
    public Optional<RolPermiso> getRolPermisoById(Long id) {
        return rolPermisoRepository.findByIdAndFechaDesasignacionPermisoIsNull(id);
    }

    @Override
    public List<RolPermiso> getAllRolPermisos() {
        return rolPermisoRepository.findAllByFechaDesasignacionPermisoIsNull();
    }

    @Override
    @Transactional
    public void deleteRolPermiso(Long id) {
        RolPermiso rolPermiso = rolPermisoRepository.findById(id)
                .orElseThrow(() -> new RolPermisoNotFoundException(id));

        rolPermiso.darDeBaja();

        rolPermisoRepository.save(rolPermiso);
    }
}