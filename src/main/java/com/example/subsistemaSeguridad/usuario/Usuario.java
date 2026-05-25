package com.example.subsistemaSeguridad.usuario;

import com.example.subsistemaSeguridad.usuario.dto.UsuarioUpdateDTO;
import com.example.subsistemaSeguridad.usuario.exception.UsuarioDadoDeBajaException;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String passwordUsuario;

    @Column(nullable = false)
    private Instant fechaAltaUsuario;

    private Instant fechaBajaUsuario;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioSistema> usuariosSistema = new ArrayList<>();

    public void actualizarDatos(UsuarioUpdateDTO dto) {
        if (dto.mailUsuario() != null) {
            setMailUsuario(dto.mailUsuario());
        }
    }

    public boolean estaDadoDeBaja() {
        return fechaBajaUsuario != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new UsuarioDadoDeBajaException(id);
        }

        fechaBajaUsuario = Instant.now();

        for (UsuarioSistema usuarioSistema : usuariosSistema) {
            if (!usuarioSistema.estaDadoDeBaja()) {
                usuarioSistema.darDeBaja();
            }
        }
    }
}
