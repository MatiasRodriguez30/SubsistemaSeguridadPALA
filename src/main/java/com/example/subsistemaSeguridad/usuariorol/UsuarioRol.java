package com.example.subsistemaSeguridad.usuariorol;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolDadoDeBajaException;

@Entity
@Table(name = "usuario_rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SUsuarioRol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "contador_usuario_rol")
    private int contadorUsuarioRol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    private Instant fechaAsignacionUsuarioRol;
    
    private Instant fechaBajaRolUsuario;

    public boolean estaDadoDeBaja() {
        return this.fechaBajaRolUsuario != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new UsuarioRolDadoDeBajaException(this.id);
        }
        this.fechaBajaRolUsuario = Instant.now();
    }
}
