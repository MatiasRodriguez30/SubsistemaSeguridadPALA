package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioCreateDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioResponseDTO;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@RequestBody @Valid UsuarioCreateDTO dto) {
        Usuario usuario = usuarioService.createUsuario(dto);
        return new ResponseEntity<>(usuarioMapper.toResponseDTO(usuario), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> ResponseEntity.ok(usuarioMapper.toResponseDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateDTO dto) {
        Usuario actualizado = usuarioService.updateUsuario(id, dto);
        return ResponseEntity.ok(usuarioMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
