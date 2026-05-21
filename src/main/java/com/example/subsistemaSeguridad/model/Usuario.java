package com.example.subsistemaSeguridad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String mailUsuario;
    
    @Column(nullable = false)
    private String passwordUsuario;
    
    @Column(nullable = false)
    private Instant fechaAltaUsuario;
    
    private Instant fechaBajaUsuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id")
    private Sistema sistema;
}
