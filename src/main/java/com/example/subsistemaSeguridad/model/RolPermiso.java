package com.example.subsistemaSeguridad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
    
    private Instant fechaAsignacionPermiso;
    
    private Instant fechaDesasignacionPermiso;
}
