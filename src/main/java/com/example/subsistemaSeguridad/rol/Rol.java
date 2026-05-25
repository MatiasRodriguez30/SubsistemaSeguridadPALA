package com.example.subsistemaSeguridad.rol;

import com.example.subsistemaSeguridad.rol.dto.RolUpdateDTO;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;
import com.example.subsistemaSeguridad.rolpermiso.RolPermiso;
import com.example.subsistemaSeguridad.sistema.Sistema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreRol;

    private String descripcionRol;

    @Column(nullable = false)
    private boolean esPredeterminada;

    @Column(nullable = false)
    private Instant fechaAltaRol;

    private Instant fechaBajaRol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id")
    private Sistema sistema;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RolPermiso> permisosRol = new ArrayList<>();

    public void actualizarDatos(RolUpdateDTO dto) {
        if (dto.nombreRol() != null) {
            setNombreRol(dto.nombreRol());
        }

        if (dto.descripcionRol() != null) {
            setDescripcionRol(dto.descripcionRol());
        }

        if (dto.esPredeterminada() != null) {
            setEsPredeterminada(dto.esPredeterminada());
        }
    }

    public boolean estaDadoDeBaja() {
        return fechaBajaRol != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(id);
        }

        fechaBajaRol = Instant.now();

        for (RolPermiso rolPermiso : permisosRol) {
            if (!rolPermiso.estaDadoDeBaja()) {
                rolPermiso.darDeBaja();
            }
        }
    }
}
