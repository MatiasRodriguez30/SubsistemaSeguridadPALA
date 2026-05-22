package com.example.subsistemaSeguridad.sistema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;

@Entity
@Table(name = "sistemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sistema {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombreSistema;
    
    @Column(unique = true, nullable = false)
    private String keySistema;
    
    @Column(nullable = false)
    private Instant fechaAltaSistema;
    
    private Instant fechaBajaSistema;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public void actualizarDatos(SistemaUpdateDTO dto) {
        if (dto.nombreSistema() != null) {
            this.setNombreSistema(dto.nombreSistema());
        }
        if (dto.keySistema() != null) {
            this.setKeySistema(dto.keySistema());
        }
    }

    public boolean estaDadoDeBaja() {
        return this.fechaBajaSistema != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(this.id);
        }
        this.fechaBajaSistema = Instant.now();
    }
}
