package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioCreateDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioUpdateDTO;
import com.example.subsistemaSeguridad.shared.EmailNormalizer;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioNotFoundException;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;      //PASWORD ENCRIPTADA

    @Autowired
    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario createUsuario(UsuarioCreateDTO dto) {
        String normalizedMail = EmailNormalizer.normalize(dto.mailUsuario());

        usuarioRepository.findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .ifPresent(existing -> {
                    throw new UsuarioYaRegistradoException(existing.getMailUsuario());
                });

        final Usuario usuario = usuarioMapper.toEntity(dto);

        String passwordEncriptada = passwordEncoder.encode(dto.passwordUsuario());
        usuario.setPasswordUsuario(passwordEncriptada);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findByIdAndFechaBajaUsuarioIsNull(id);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAllByFechaBajaUsuarioIsNull();
    }

    @Override
    @Transactional
    public Usuario updateUsuario(Long id, UsuarioUpdateDTO dto) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (usuario.estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(id);
        }

        if (dto.mailUsuario() != null) {
            usuario.setMailUsuario(EmailNormalizer.normalize(dto.mailUsuario()));
        }

        if (dto.passwordUsuario() != null) {
            String passwordEncriptada = passwordEncoder.encode(dto.passwordUsuario());
            usuario.setPasswordUsuario(passwordEncriptada);
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Long id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        usuario.darDeBaja();
        usuarioRepository.save(usuario);
    }
}
