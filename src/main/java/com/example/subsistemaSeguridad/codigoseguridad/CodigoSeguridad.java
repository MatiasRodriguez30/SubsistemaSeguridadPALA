package com.example.subsistemaSeguridad.codigoseguridad;

import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "codigos_seguridad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_sistema_id")
    private UsuarioSistema usuarioSistema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCodigoSeguridad tipo;

    @Column(nullable = false)
    private String codigoHash;

    @Column(nullable = false)
    private Instant fechaAlta;

    @Column(nullable = false)
    private Instant fechaExpiracion;

    private Instant fechaUso;

    @Builder.Default
    @Column(nullable = false)
    private int intentos = 0;

    public boolean estaUsado() {
        return fechaUso != null;
    }

    public boolean estaExpirado(Instant ahora) {
        return !fechaExpiracion.isAfter(ahora);
    }

    public void registrarIntento() {
        intentos++;
    }

    public void marcarUsado() {
        fechaUso = Instant.now();
    }
}
