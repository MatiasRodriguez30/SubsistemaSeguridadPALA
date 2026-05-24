package com.example.subsistemaSeguridad.rolpermiso;

import com.example.subsistemaSeguridad.permiso.Permiso;
import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.rolpermiso.exception.RolPermisoDadoDeBajaException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

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