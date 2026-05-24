package com.example.subsistemaSeguridad.rol;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.rolpermiso.RolPermiso;
import com.example.subsistemaSeguridad.rol.dto.RolUpdateDTO;
import com.example.subsistemaSeguridad.rol.exception.RolDadoDeBajaException;

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
    private Instant fechaAltaRol;
    
    private Instant fechaBajaRol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id")
    private Sistema sistema;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    private List<RolPermiso> permisosRol = new ArrayList<>();

    public void actualizarDatos(RolUpdateDTO dto) {
        if (dto.nombreRol() != null) {
            this.setNombreRol(dto.nombreRol());
        }

        if (dto.descripcionRol() != null) {
            this.setDescripcionRol(dto.descripcionRol());
        }
    }

    public boolean estaDadoDeBaja() {
        return this.fechaBajaRol != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new RolDadoDeBajaException(this.id);
        }

        this.fechaBajaRol = Instant.now();

        if (this.permisosRol != null) {
            for (RolPermiso rp : this.permisosRol) {
                if (!rp.estaDadoDeBaja()) {
                    rp.darDeBaja();
                }
            }
        }
    }
}