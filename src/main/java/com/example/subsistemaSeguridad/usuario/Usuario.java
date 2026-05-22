package com.example.subsistemaSeguridad.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

import com.example.subsistemaSeguridad.sistema.Sistema;
import com.example.subsistemaSeguridad.usuariorol.UsuarioRol;
import com.example.subsistemaSeguridad.usuario.dto.UsuarioUpdateDTO;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id")
    private List<UsuarioRol> rolesUsuario;

    public void actualizarDatos(UsuarioUpdateDTO dto) {
        if (dto.mailUsuario() != null) {
            this.setMailUsuario(dto.mailUsuario());
        }
        if (dto.passwordUsuario() != null) {
            this.setPasswordUsuario(dto.passwordUsuario());
        }
    }

    public boolean estaDadoDeBaja() {
        return this.fechaBajaUsuario != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(this.id);
        }
        this.fechaBajaUsuario = Instant.now();
        if (this.rolesUsuario != null) {
            for (UsuarioRol ur : this.rolesUsuario) {
                if (!ur.estaDadoDeBaja()) {
                    ur.darDeBaja();
                }
            }
        }
    }
}
