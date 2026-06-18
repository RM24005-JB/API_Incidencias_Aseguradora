package com.insurances.service;

import com.insurances.exception.TokenRefreshException;
import com.insurances.model.RefreshToken;
import com.insurances.model.Usuario;
import com.insurances.repository.RefreshTokenRepository;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    public RefreshToken createRefreshToken(Long userId) {
        Usuario user = usuarioRepository.findById(userId).orElseThrow();
        refreshTokenRepository.deleteByUsuario(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expirado. Por favor inicie sesion nuevamente.");
        }
        return token;
    }

    // FIX: @Transactional(readOnly = true) ya que solo lee
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token no encontrado"));
    }
}
