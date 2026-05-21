package com.example.subsistemaSeguridad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rol_id")
    private java.util.List<RolPermiso> permisosRol;
}
