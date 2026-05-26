package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterRequestDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO register(RegisterRequestDTO request);

    LoginResponseDTO registerExternal(String systemKey, ExternalRegisterRequestDTO request);

    LoginResponseDTO loginExternal(String systemKey, LoginRequestDTO request);
}
