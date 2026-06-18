package com.insurances.service;

import com.insurances.config.JwtUtil;
import com.insurances.dto.*;
import com.insurances.exception.BusinessException;
import com.insurances.exception.UnauthorizedException;
import com.insurances.model.RefreshToken;
import com.insurances.model.Role;
import com.insurances.model.Usuario;
import com.insurances.repository.RefreshTokenRepository;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya esta registrado");
        }

        // FIX #1: Usuario.builder() ahora respeta los @Builder.Default
        // No es necesario .role(Role.USER) ni .enabled(true) gracias a @Builder.Default
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .build();

        usuarioRepository.save(usuario);
        usuarioRepository.flush(); // Forzar flush para asegurar disponibilidad inmediata

        log.info("Usuario registrado exitosamente: {}", request.getEmail());

        // Autenticar inmediatamente despues del registro
        return authenticateAndGenerateTokens(request.getEmail(), request.getPassword());
    }

    @Transactional
    public JwtResponse login(LoginRequest request) {
        return authenticateAndGenerateTokens(request.getEmail(), request.getPassword());
    }

    private JwtResponse authenticateAndGenerateTokens(String email, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Credenciales invalidas");
        } catch (DisabledException e) {
            throw new UnauthorizedException("Usuario deshabilitado");
        } catch (Exception e) {
            log.error("Error de autenticacion: {}", e.getMessage());
            throw new UnauthorizedException("Error en autenticacion");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(getUserIdFromEmail(email));

        log.info("Login exitoso para usuario: {}", email);
        return new JwtResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        Usuario user = refreshToken.getUsuario();
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        String role = user.getRole().name();
        String newAccessToken = jwtUtil.generateAccessTokenWithRole(user.getEmail(), role);
        return new JwtResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getCurrentUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return toDTO(usuario);
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        Usuario usuario = token.getUsuario();
        refreshTokenRepository.deleteByUsuario(usuario);
        SecurityContextHolder.clearContext();
        log.info("Logout exitoso para usuario: {}", usuario.getEmail());
    }

    private Long getUserIdFromEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"))
                .getId();
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setTelefono(usuario.getTelefono());
        dto.setRole(usuario.getRole());
        dto.setEnabled(usuario.isEnabled());
        return dto;
    }
}
