package com.example.subsistemaSeguridad.sistema;

import com.example.subsistemaSeguridad.sistema.dto.SistemaCreateDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;
import com.example.subsistemaSeguridad.sistema.exception.SistemaNotFoundException;
import com.example.subsistemaSeguridad.shared.EmailNormalizer;
import com.example.subsistemaSeguridad.usuario.UsuarioRepository;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SistemaServiceImpl implements SistemaService {

    private final SistemaRepository sistemaRepository;
    private final SistemaMapper sistemaMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSistemaRepository usuarioSistemaRepository;

    @Autowired
    public SistemaServiceImpl(
            SistemaRepository sistemaRepository,
            SistemaMapper sistemaMapper,
            UsuarioRepository usuarioRepository,
            UsuarioSistemaRepository usuarioSistemaRepository
    ) {
        this.sistemaRepository = sistemaRepository;
        this.sistemaMapper = sistemaMapper;
        this.usuarioRepository = usuarioRepository;
        this.usuarioSistemaRepository = usuarioSistemaRepository;
    }

    @Override
    @Transactional
    public Sistema createSistema(SistemaCreateDTO dto) {
        final Sistema sistema = sistemaMapper.toEntity(dto);
        sistema.asignarKeySistema(generarKeySistema(dto.nombreSistema()));
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
    public Optional<Sistema> getSistemaVisibleParaUsuario(Long id, String mailUsuario) {
        final String normalizedMail = EmailNormalizer.normalize(mailUsuario);

        return usuarioRepository.findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .flatMap(usuario -> {
                    final Optional<Sistema> sistemaPropio = sistemaRepository
                            .findByIdAndUsuarioIdAndFechaBajaSistemaIsNull(id, usuario.getId());

                    if (sistemaPropio.isPresent()) {
                        return sistemaPropio;
                    }

                    return usuarioSistemaRepository
                            .findByUsuarioIdAndSistemaIdAndFechaBajaUsuarioSistemaIsNull(usuario.getId(), id)
                            .map(UsuarioSistema::getSistema)
                            .filter(sistema -> !sistema.estaDadoDeBaja());
                });
    }

    @Override
    public List<Sistema> getSistemasVisiblesParaUsuario(String mailUsuario) {
        final String normalizedMail = EmailNormalizer.normalize(mailUsuario);

        return usuarioRepository.findByMailUsuarioAndFechaBajaUsuarioIsNull(normalizedMail)
                .map(usuario -> {
                    final Map<Long, Sistema> visibles = new LinkedHashMap<>();

                    sistemaRepository.findAllByUsuarioIdAndFechaBajaSistemaIsNull(usuario.getId())
                            .forEach(sistema -> visibles.put(sistema.getId(), sistema));

                    usuarioSistemaRepository.findAllByUsuarioIdAndFechaBajaUsuarioSistemaIsNull(usuario.getId())
                            .stream()
                            .map(UsuarioSistema::getSistema)
                            .filter(sistema -> sistema != null && !sistema.estaDadoDeBaja())
                            .forEach(sistema -> visibles.put(sistema.getId(), sistema));

                    return visibles.values().stream().toList();
                })
                .orElse(List.of());
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

    private String generarKeySistema(String nombreSistema) {
        String baseKey = Sistema.normalizarBaseKey(nombreSistema);
        
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits of entropy
        random.nextBytes(bytes);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return baseKey + "_SYS_" + hexString.toString().toUpperCase();
    }
}
