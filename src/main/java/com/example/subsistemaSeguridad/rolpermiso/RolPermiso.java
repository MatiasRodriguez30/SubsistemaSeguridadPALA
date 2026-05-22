package com.example.subsistemaSeguridad.rolpermiso;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

import com.example.subsistemaSeguridad.permiso.Permiso;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoDadoDeBajaException;

@Entity
@Table(name = "rol_permiso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "contador_permiso")
    private int contadorPermiso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
    
    private Instant fechaAsignacionPermiso;
    
    private Instant fechaDesasignacionPermiso;

    public boolean estaDadoDeBaja() {
        return this.fechaDesasignacionPermiso != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new RolPermisoDadoDeBajaException(this.id);
        }
        this.fechaDesasignacionPermiso = Instant.now();
    }
}
