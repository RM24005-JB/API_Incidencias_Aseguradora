package com.insurances.service;

import com.insurances.dto.UsuarioDTO;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Role;
import com.insurances.model.Usuario;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO cambiarRol(Long id, Role nuevoRol) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        u.setRole(nuevoRol);
        return toDTO(usuarioRepository.save(u));
    }

    public void toggleEnable(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        u.setEnabled(!u.isEnabled());
        usuarioRepository.save(u);
    }

    public UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setNombre(u.getNombre());
        dto.setTelefono(u.getTelefono());
        dto.setRole(u.getRole());
        dto.setEnabled(u.isEnabled());
        return dto;
    }
}