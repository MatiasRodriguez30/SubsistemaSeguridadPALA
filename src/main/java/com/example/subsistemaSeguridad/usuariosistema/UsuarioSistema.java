package com.example.subsistemaSeguridad.usuariosistema;

import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuariorol.UsuarioRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.subsistemaSeguridad.usuariosistema.dto.UsuarioSistemaUpdateDTO;

@Entity
@Table(name = "usuario_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id", nullable = false)
    private Sistema sistema;

    @Column(nullable = false)
    private Instant fechaAltaUsuarioSistema;

    private Instant fechaBajaUsuarioSistema;

    @Column(nullable = false)
    private String passwordUsuarioSistema;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioRol> rolesUsuarioSistema = new ArrayList<>();

    public boolean estaDadoDeBaja() {
        return fechaBajaUsuarioSistema != null;
    }

    public void actualizarDatos(UsuarioSistemaUpdateDTO dto) {
        if (dto.passwordUsuarioSistema() != null) {
            actualizarPasswordUsuarioSistema(dto.passwordUsuarioSistema());
        }
    }

    public void actualizarPasswordUsuarioSistema(String passwordUsuarioSistema) {
        this.passwordUsuarioSistema = passwordUsuarioSistema;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            return;
        }

        fechaBajaUsuarioSistema = Instant.now();

        for (UsuarioRol usuarioRol : rolesUsuarioSistema) {
            if (!usuarioRol.estaDadoDeBaja()) {
                usuarioRol.darDeBaja();
            }
        }
    }

    public void reactivar(String passwordUsuarioSistema) {
        this.fechaBajaUsuarioSistema = null;
        this.passwordUsuarioSistema = passwordUsuarioSistema;
        this.fechaAltaUsuarioSistema = Instant.now();
    }
}
