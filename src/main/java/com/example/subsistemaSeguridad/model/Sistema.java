package com.example.subsistemaSeguridad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

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
}
