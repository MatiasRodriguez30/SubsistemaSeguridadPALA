package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.ForgotPasswordRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.RegisterResponseDTO;
import com.example.subsistemaSeguridad.auth.dto.ResetPasswordRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.VerifyEmailRequestDTO;
import com.example.subsistemaSeguridad.shared.dto.MessageResponse;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    RegisterResponseDTO register(RegisterRequestDTO request);

    LoginResponseDTO verifyEmail(VerifyEmailRequestDTO request);

    MessageResponse resendVerification(ForgotPasswordRequestDTO request);

    MessageResponse forgotPassword(ForgotPasswordRequestDTO request);

    MessageResponse resetPassword(ResetPasswordRequestDTO request);

    ExternalRegisterResponseDTO registerExternal(String systemKey, ExternalRegisterRequestDTO request);

    LoginResponseDTO loginExternal(String systemKey, LoginRequestDTO request);

    LoginResponseDTO verifyExternalEmail(String systemKey, VerifyEmailRequestDTO request);

    MessageResponse resendExternalVerification(String systemKey, ForgotPasswordRequestDTO request);

    MessageResponse forgotExternalPassword(String systemKey, ForgotPasswordRequestDTO request);

    MessageResponse resetExternalPassword(String systemKey, ResetPasswordRequestDTO request);
}
