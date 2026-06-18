package com.insurances.controller;

import com.insurances.dto.JwtResponse;
import com.insurances.dto.LoginRequest;
import com.insurances.dto.RefreshTokenRequest;
import com.insurances.dto.RegisterRequest;
import com.insurances.dto.UsuarioDTO;
import com.insurances.exception.UnauthorizedException;
import com.insurances.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        return ResponseEntity.ok(
                authService.getCurrentUser(userDetails.getUsername())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }
}