package com.example.subsistemaSeguridad.permiso;

import com.example.subsistemaSeguridad.permiso.dto.PermisoCreateDTO;
import com.example.subsistemaSeguridad.permiso.dto.PermisoResponseDTO;
import com.example.subsistemaSeguridad.permiso.dto.PermisoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;
    private final PermisoMapper permisoMapper;

    @Autowired
    public PermisoController(PermisoService permisoService, PermisoMapper permisoMapper) {
        this.permisoService = permisoService;
        this.permisoMapper = permisoMapper;
    }

    @PostMapping
    public ResponseEntity<PermisoResponseDTO> createPermiso(@RequestBody @Valid PermisoCreateDTO dto) {
        Permiso permiso = permisoService.createPermiso(dto);
        return new ResponseEntity<>(permisoMapper.toResponseDTO(permiso), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> getPermisoById(@PathVariable Long id) {
        return permisoService.getPermisoById(id)
                .map(permiso -> ResponseEntity.ok(permisoMapper.toResponseDTO(permiso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PermisoResponseDTO>> getAllPermisos() {
        List<PermisoResponseDTO> permisos = permisoService.getAllPermisos().stream()
                .map(permisoMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permisos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> updatePermiso(@PathVariable Long id, @RequestBody @Valid PermisoUpdateDTO dto) {
        Permiso actualizado = permisoService.updatePermiso(id, dto);
        return ResponseEntity.ok(permisoMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermiso(@PathVariable Long id) {
        permisoService.deletePermiso(id);
        return ResponseEntity.noContent().build();
    }
}
