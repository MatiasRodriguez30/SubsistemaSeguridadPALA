package com.example.subsistemaSeguridad.sistema;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.sistema.dto.SistemaUpdateDTO;
import com.example.subsistemaSeguridad.sistema.exception.SistemaDadoDeBajaException;

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

    private static final Pattern KEY_INVALID_PATTERN = Pattern.compile("[^A-Z0-9_]");

    public void actualizarDatos(SistemaUpdateDTO dto) {
        Optional.ofNullable(dto.nombreSistema()).ifPresent(this::setNombreSistema);
    }

    public void asignarKeySistema(String keySistema) {
        this.keySistema = keySistema;
    }

    public static String normalizarBaseKey(String nombreSistema) {
        String normalized = java.text.Normalizer.normalize(nombreSistema, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');

        normalized = KEY_INVALID_PATTERN.matcher(normalized).replaceAll("");
        normalized = normalized.replaceAll("_+", "_").replaceAll("^_|_$", "");

        if (normalized.isBlank()) {
            return "SISTEMA";
        }

        return normalized;
    }

    public boolean estaDadoDeBaja() {
        return this.fechaBajaSistema != null;
    }

    public void darDeBaja() {
        if (estaDadoDeBaja()) {
            throw new SistemaDadoDeBajaException(this.id);
        }
        this.fechaBajaSistema = Instant.now();
    }
}
