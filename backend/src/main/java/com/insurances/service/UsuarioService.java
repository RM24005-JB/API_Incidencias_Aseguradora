package com.insurances.service;

import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Usuario;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Long getIdByEmail(String email) {
        return findByEmail(email).getId();
    }
}