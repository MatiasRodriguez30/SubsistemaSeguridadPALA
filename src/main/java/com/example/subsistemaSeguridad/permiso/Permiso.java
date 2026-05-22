package com.example.subsistemaSeguridad.permiso;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.permiso.dto.PermisoUpdateDTO;
import com.example.subsistemaSeguridad.permiso.exception.PermisoDadoDeBajaException;

@Entity
@Table(name = "permisos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombrePermiso;
    
    @Column(nullable = false)
    private Instant fechaAltaPermiso;
    
    private Instant fechaBajaPermiso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id", nullable = false)
    private Sistema sistema;

    public void actualizarDatos(PermisoUpdateDTO dto) {
        if (dto.nombrePermiso() != null) {
            this.setNombrePermiso(dto.nombrePermiso());
        }
    }

    public boolean estaDadoDeBaja() {
        return this.fechaBajaPermiso != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new PermisoDadoDeBajaException(this.id);
        }
        this.fechaBajaPermiso = Instant.now();
    }
}
