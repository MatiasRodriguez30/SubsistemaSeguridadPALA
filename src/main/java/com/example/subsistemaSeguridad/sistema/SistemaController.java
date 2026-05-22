package com.example.subsistemaSeguridad.sistema;

import com.example.subsistemaSeguridad.sistema.dto.SistemaCreateDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaResponseDTO;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sistemas")
public class SistemaController {

    private final SistemaService sistemaService;
    private final SistemaMapper sistemaMapper;

    @Autowired
    public SistemaController(SistemaService sistemaService, SistemaMapper sistemaMapper) {
        this.sistemaService = sistemaService;
        this.sistemaMapper = sistemaMapper;
    }

    @PostMapping
    public ResponseEntity<SistemaResponseDTO> createSistema(@RequestBody @Valid SistemaCreateDTO dto) {
        Sistema sistema = sistemaService.createSistema(dto);
        return new ResponseEntity<>(sistemaMapper.toResponseDTO(sistema), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SistemaResponseDTO> getSistemaById(@PathVariable Long id) {
        return sistemaService.getSistemaById(id)
                .map(sistema -> ResponseEntity.ok(sistemaMapper.toResponseDTO(sistema)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SistemaResponseDTO>> getAllSistemas() {
        List<SistemaResponseDTO> sistemas = sistemaService.getAllSistemas().stream()
                .map(sistemaMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sistemas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SistemaResponseDTO> updateSistema(@PathVariable Long id, @RequestBody @Valid SistemaUpdateDTO dto) {
        Sistema actualizado = sistemaService.updateSistema(id, dto);
        return ResponseEntity.ok(sistemaMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSistema(@PathVariable Long id) {
        sistemaService.deleteSistema(id);
        return ResponseEntity.noContent().build();
    }
}
