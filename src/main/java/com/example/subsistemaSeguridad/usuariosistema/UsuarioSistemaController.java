package com.example.subsistemaSeguridad.usuariosistema;

import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaCreateDTO;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaResponseDTO;
import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuario-sistemas")
public class UsuarioSistemaController {

    private final UsuarioSistemaService usuarioSistemaService;
    private final UsuarioSistemaMapper usuarioSistemaMapper;

    @Autowired
    public UsuarioSistemaController(
            UsuarioSistemaService usuarioSistemaService,
            UsuarioSistemaMapper usuarioSistemaMapper
    ) {
        this.usuarioSistemaService = usuarioSistemaService;
        this.usuarioSistemaMapper = usuarioSistemaMapper;
    }

    @PostMapping
    public ResponseEntity<UsuarioSistemaResponseDTO> createUsuarioSistema(
            @RequestBody @Valid UsuarioSistemaCreateDTO dto
    ) {
        UsuarioSistema usuarioSistema = usuarioSistemaService.createUsuarioSistema(dto);
        return new ResponseEntity<>(usuarioSistemaMapper.toResponseDTO(usuarioSistema), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaResponseDTO> getUsuarioSistemaById(@PathVariable Long id) {
        return usuarioSistemaService.getUsuarioSistemaById(id)
                .map(usuarioSistema -> ResponseEntity.ok(usuarioSistemaMapper.toResponseDTO(usuarioSistema)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSistemaResponseDTO>> getUsuarioSistemas(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long sistemaId
    ) {
        List<UsuarioSistema> entities;

        if (usuarioId != null) {
            entities = usuarioSistemaService.getUsuariosSistemaByUsuarioId(usuarioId);
        } else if (sistemaId != null) {
            entities = usuarioSistemaService.getUsuariosSistemaBySistemaId(sistemaId);
        } else {
            entities = usuarioSistemaService.getAllUsuariosSistema();
        }

        List<UsuarioSistemaResponseDTO> usuarioSistemas = entities.stream()
                .map(usuarioSistemaMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarioSistemas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSistemaResponseDTO> updateUsuarioSistema(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioSistemaUpdateDTO dto
    ) {
        UsuarioSistema actualizado = usuarioSistemaService.updateUsuarioSistema(id, dto);
        return ResponseEntity.ok(usuarioSistemaMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuarioSistema(@PathVariable Long id) {
        usuarioSistemaService.deleteUsuarioSistema(id);
        return ResponseEntity.noContent().build();
    }
}
