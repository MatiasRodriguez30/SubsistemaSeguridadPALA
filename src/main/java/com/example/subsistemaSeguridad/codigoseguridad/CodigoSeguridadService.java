package com.example.subsistemaSeguridad.codigoseguridad;

import com.example.subsistemaSeguridad.usuario.Usuario;
import com.example.subsistemaSeguridad.usuariosistema.UsuarioSistema;

public interface CodigoSeguridadService {

    void emitirCodigoVerificacionCorreo(UsuarioSistema usuarioSistema);

    void emitirCodigoVerificacionCorreo(Usuario usuario);

    void emitirCodigoRecuperacionPassword(UsuarioSistema usuarioSistema);

    void emitirCodigoRecuperacionPassword(Usuario usuario);

    void validarCodigo(UsuarioSistema usuarioSistema, TipoCodigoSeguridad tipo, String codigo);

    void validarCodigo(Usuario usuario, TipoCodigoSeguridad tipo, String codigo);
}
