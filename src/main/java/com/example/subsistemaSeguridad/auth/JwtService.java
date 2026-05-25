package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.UsuarioAutenticadoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET_KEY = "a8f7c2d9e1b4a6c3f9d0e7t2c5a1f8e8e6c9b3a7e0f2c8d1a2l1e6f3c7d4a2b8";
    private static final long EXPIRATION_MS = 1000 * 60 * 60;

    public String generarToken(UsuarioAutenticadoDTO usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(usuario.subjectId().toString())
                .claim("mail", usuario.mailUsuario())
                .claim("roles", usuario.roles())
                .claim("permisos", usuario.permisos())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extraerSubjectId(String token) {
        return Long.valueOf(extraerClaims(token).getSubject());
    }

    public String extraerMail(String token) {
        return extraerClaims(token).get("mail", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        return extraerClaims(token).get("roles", List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerPermisos(String token) {
        return extraerClaims(token).get("permisos", List.class);
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
