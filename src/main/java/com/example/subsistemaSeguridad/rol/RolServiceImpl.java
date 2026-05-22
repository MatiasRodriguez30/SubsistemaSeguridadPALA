package com.example.subsistemaSeguridad.rol;

import com.example.subsistemaSeguridad.rol.dto.RolCreateDTO;
import com.example.subsistemaSeguridad.rol.dto.RolUpdateDTO;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.rol.exception.RolNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    @Transactional
    public Rol createRol(RolCreateDTO dto) {
        final Rol rol = rolMapper.toEntity(dto);
        return rolRepository.save(rol);
    }

    @Override
    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findByIdAndFechaBajaRolIsNull(id);
    }

    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAllByFechaBajaRolIsNull();
    }

    @Override
    @Transactional
    public Rol updateRol(Long id, RolUpdateDTO dto) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException(id));

        if (rol.estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(id);
        }

        rol.actualizarDatos(dto);

        return rolRepository.save(rol);
    }

    @Override
    @Transactional
    public void deleteRol(Long id) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException(id));

        rol.darDeBaja();
        rolRepository.save(rol);
    }
}
