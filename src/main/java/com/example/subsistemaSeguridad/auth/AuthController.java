package com.example.subsistemaSeguridad.auth;

import com.example.subsistemaSeguridad.auth.dto.ExternalRegisterRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginRequestDTO;
import com.example.subsistemaSeguridad.auth.dto.LoginResponseDTO;
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

    @PostMapping("/external/register")
    public ResponseEntity<LoginResponseDTO> registerExternal(
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
}
