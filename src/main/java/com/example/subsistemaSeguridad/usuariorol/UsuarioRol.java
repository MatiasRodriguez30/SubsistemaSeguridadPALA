package com.example.subsistemaSeguridad.usuariorol;

import com.example.subsistemaSeguridad.rol.Rol;
import com.example.subsistemaSeguridad.usuariorol.exception.UsuarioRolDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "usuario_rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contador_usuario_rol")
    private int contadorUsuarioRol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_sistema_id", nullable = false)
    private UsuarioSistema usuarioSistema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private Instant fechaAsignacionUsuarioRol;

    private Instant fechaBajaRolUsuario;

    public boolean estaDadoDeBaja() {
        return fechaBajaRolUsuario != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new UsuarioRolDadoDeBajaException(id);
        }
        fechaBajaRolUsuario = Instant.now();
    }
}
