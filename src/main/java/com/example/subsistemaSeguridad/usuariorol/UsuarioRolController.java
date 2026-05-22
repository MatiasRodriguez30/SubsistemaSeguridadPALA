package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolCreateDTO;
import com.example.subsistemaSeguridad.usuariorol.dto.UsuarioRolResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuario-roles")
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;
    private final UsuarioRolMapper usuarioRolMapper;

    @Autowired
    public UsuarioRolController(UsuarioRolService usuarioRolService, UsuarioRolMapper usuarioRolMapper) {
        this.usuarioRolService = usuarioRolService;
        this.usuarioRolMapper = usuarioRolMapper;
    }

    @PostMapping
    public ResponseEntity<UsuarioRolResponseDTO> createUsuarioRol(@RequestBody @Valid UsuarioRolCreateDTO dto) {
        UsuarioRol usuarioRol = usuarioRolService.createUsuarioRol(dto);
        return new ResponseEntity<>(usuarioRolMapper.toResponseDTO(usuarioRol), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> getUsuarioRolById(@PathVariable Long id) {
        return usuarioRolService.getUsuarioRolById(id)
                .map(usuarioRol -> ResponseEntity.ok(usuarioRolMapper.toResponseDTO(usuarioRol)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRolResponseDTO>> getAllUsuarioRoles() {
        List<UsuarioRolResponseDTO> usuarioRoles = usuarioRolService.getAllUsuarioRoles().stream()
                .map(usuarioRolMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarioRoles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuarioRol(@PathVariable Long id) {
        usuarioRolService.deleteUsuarioRol(id);
        return ResponseEntity.noContent().build();
    }
}
