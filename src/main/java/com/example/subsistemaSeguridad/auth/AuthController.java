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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<LoginResponseDTO> verifyEmail(@RequestBody @Valid VerifyEmailRequestDTO request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.resendVerification(request));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/external/register")
    public ResponseEntity<ExternalRegisterResponseDTO> registerExternal(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid ExternalRegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authService.registerExternal(systemKey, request));
    }

    @PostMapping("/external/login")
    public ResponseEntity<LoginResponseDTO> loginExternal(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid LoginRequestDTO request
    ) {
        return ResponseEntity.ok(authService.loginExternal(systemKey, request));
    }

    @PostMapping("/external/verify-email")
    public ResponseEntity<LoginResponseDTO> verifyExternalEmail(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid VerifyEmailRequestDTO request
    ) {
        return ResponseEntity.ok(authService.verifyExternalEmail(systemKey, request));
    }

    @PostMapping("/external/resend-verification")
    public ResponseEntity<MessageResponse> resendExternalVerification(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid ForgotPasswordRequestDTO request
    ) {
        return ResponseEntity.ok(authService.resendExternalVerification(systemKey, request));
    }

    @PostMapping("/external/password/forgot")
    public ResponseEntity<MessageResponse> forgotExternalPassword(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid ForgotPasswordRequestDTO request
    ) {
        return ResponseEntity.ok(authService.forgotExternalPassword(systemKey, request));
    }

    @PostMapping("/external/password/reset")
    public ResponseEntity<MessageResponse> resetExternalPassword(
            @RequestHeader("X-System-Key") String systemKey,
            @RequestBody @Valid ResetPasswordRequestDTO request
    ) {
        return ResponseEntity.ok(authService.resetExternalPassword(systemKey, request));
    }
}
