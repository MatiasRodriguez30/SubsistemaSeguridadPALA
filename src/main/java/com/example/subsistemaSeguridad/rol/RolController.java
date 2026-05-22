package com.example.subsistemaSeguridad.rol;

import com.example.subsistemaSeguridad.rol.dto.RolCreateDTO;
import com.example.subsistemaSeguridad.rol.dto.RolResponseDTO;
import com.example.subsistemaSeguridad.rol.dto.RolUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;
    private final RolMapper rolMapper;

    @Autowired
    public RolController(RolService rolService, RolMapper rolMapper) {
        this.rolService = rolService;
        this.rolMapper = rolMapper;
    }

    @PostMapping
    public ResponseEntity<RolResponseDTO> createRol(@RequestBody @Valid RolCreateDTO dto) {
        Rol rol = rolService.createRol(dto);
        return new ResponseEntity<>(rolMapper.toResponseDTO(rol), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> getRolById(@PathVariable Long id) {
        return rolService.getRolById(id)
                .map(rol -> ResponseEntity.ok(rolMapper.toResponseDTO(rol)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> getAllRoles() {
        List<RolResponseDTO> roles = rolService.getAllRoles().stream()
                .map(rolMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDTO> updateRol(@PathVariable Long id, @RequestBody @Valid RolUpdateDTO dto) {
        Rol actualizado = rolService.updateRol(id, dto);
        return ResponseEntity.ok(rolMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
        return ResponseEntity.noContent().build();
    }
}
