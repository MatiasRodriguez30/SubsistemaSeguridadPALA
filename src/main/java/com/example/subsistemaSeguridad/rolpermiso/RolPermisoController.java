package com.example.subsistemaSeguridad.rolpermiso;

import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoCreateDTO;
import com.example.subsistemaSeguridad.rolpermiso.dto.RolPermisoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rol-permisos")
public class RolPermisoController {

    private final RolPermisoService rolPermisoService;
    private final RolPermisoMapper rolPermisoMapper;

    @Autowired
    public RolPermisoController(RolPermisoService rolPermisoService, RolPermisoMapper rolPermisoMapper) {
        this.rolPermisoService = rolPermisoService;
        this.rolPermisoMapper = rolPermisoMapper;
    }

    @PostMapping
    public ResponseEntity<RolPermisoResponseDTO> createRolPermiso(@RequestBody @Valid RolPermisoCreateDTO dto) {
        RolPermiso rolPermiso = rolPermisoService.createRolPermiso(dto);
        return new ResponseEntity<>(rolPermisoMapper.toResponseDTO(rolPermiso), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolPermisoResponseDTO> getRolPermisoById(@PathVariable Long id) {
        return rolPermisoService.getRolPermisoById(id)
                .map(rolPermiso -> ResponseEntity.ok(rolPermisoMapper.toResponseDTO(rolPermiso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RolPermisoResponseDTO>> getAllRolPermisos() {
        List<RolPermisoResponseDTO> rolPermisos = rolPermisoService.getAllRolPermisos().stream()
                .map(rolPermisoMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rolPermisos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRolPermiso(@PathVariable Long id) {
        rolPermisoService.deleteRolPermiso(id);
        return ResponseEntity.noContent().build();
    }
}
