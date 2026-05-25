package com.example.subsistemaSeguridad.usuariosistema;

import com.example.subsistemaSeguridad.rol.RolRepository;
import com.example.subsistemaSeguridad.usuariorol.UsuarioRol;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaCreateDTO;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaUpdateDTO;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaNotFoundException;
import com.example.subsistemaSeguridad.usuariosistema.exception.UsuarioSistemaYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioSistemaServiceImpl implements UsuarioSistemaService {

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final UsuarioSistemaMapper usuarioSistemaMapper;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    @Autowired
    public UsuarioSistemaServiceImpl(
            UsuarioSistemaRepository usuarioSistemaRepository,
            UsuarioSistemaMapper usuarioSistemaMapper,
            PasswordEncoder passwordEncoder,
            RolRepository rolRepository
    ) {
        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.usuarioSistemaMapper = usuarioSistemaMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public UsuarioSistema createUsuarioSistema(UsuarioSistemaCreateDTO dto) {
        usuarioSistemaRepository
                .findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(dto.usuarioId(), dto.sistemaId())
                .ifPresent(existing -> {
                    throw new UsuarioSistemaYaRegistradoException(dto.usuarioId(), dto.sistemaId());
                });

        UsuarioSistema usuarioSistema = usuarioSistemaMapper.toEntity(dto);
        usuarioSistema.actualizarPasswordUsuarioSistema(passwordEncoder.encode(dto.passwordUsuarioSistema()));

        rolRepository.findBySistemaIdAndEsPredeterminadaTrueAndFechaBajaRolIsNull(dto.sistemaId())
                .ifPresent(rol -> {
                    int contador = usuarioSistema.getRolesUsuarioSistema().size() + 1;
                    UsuarioRol usuarioRol = UsuarioRol.builder()
                            .usuarioSistema(usuarioSistema)
                            .rol(rol)
                            .contadorUsuarioRol(contador)
                            .fechaAsignacionUsuarioRol(Instant.now())
                            .build();
                    usuarioSistema.getRolesUsuarioSistema().add(usuarioRol);
                });

        return usuarioSistemaRepository.save(usuarioSistema);
    }

    @Override
    public Optional<UsuarioSistema> getUsuarioSistemaById(Long id) {
        return usuarioSistemaRepository.findByIdAndFechaBajaUsuarioSistemaIsNull(id);
    }

    @Override
    public List<UsuarioSistema> getAllUsuariosSistema() {
        return usuarioSistemaRepository.findAllByFechaBajaUsuarioSistemaIsNull();
    }

    @Override
    public List<UsuarioSistema> getUsuariosSistemaByUsuarioId(Long usuarioId) {
        return usuarioSistemaRepository.findAllByUsuarioIdAndFechaBajaUsuarioSistemaIsNull(usuarioId);
    }

    @Override
    public List<UsuarioSistema> getUsuariosSistemaBySistemaId(Long sistemaId) {
        return usuarioSistemaRepository.findAllBySistemaIdAndFechaBajaUsuarioSistemaIsNull(sistemaId);
    }

    @Override
    @Transactional
    public UsuarioSistema updateUsuarioSistema(Long id, UsuarioSistemaUpdateDTO dto) {
        UsuarioSistema usuarioSistema = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new UsuarioSistemaNotFoundException(id));

        if (usuarioSistema.estaDadoDeBaja()) {
            throw new UsuarioSistemaDadoDeBajaException(id);
        }

        usuarioSistema.actualizarPasswordUsuarioSistema(passwordEncoder.encode(dto.passwordUsuarioSistema()));

        return usuarioSistemaRepository.save(usuarioSistema);
    }

    @Override
    @Transactional
    public void deleteUsuarioSistema(Long id) {
        UsuarioSistema usuarioSistema = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new UsuarioSistemaNotFoundException(id));

        if (usuarioSistema.estaDadoDeBaja()) {
            throw new UsuarioSistemaDadoDeBajaException(id);
        }

        usuarioSistema.darDeBaja();
        usuarioSistemaRepository.save(usuarioSistema);
    }
}
