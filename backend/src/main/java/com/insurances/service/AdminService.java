package com.insurances.service;

import com.insurances.dto.UsuarioDTO;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Poliza;
import com.insurances.model.Reclamo;
import com.insurances.model.RefreshToken;
import com.insurances.model.Role;
import com.insurances.model.Usuario;
import com.insurances.repository.PolizaRepository;
import com.insurances.repository.ReclamoRepository;
import com.insurances.repository.RefreshTokenRepository;
import com.insurances.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UsuarioRepository usuarioRepository;
    private final PolizaRepository polizaRepository;
    private final ReclamoRepository reclamoRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Eliminar reclamos asociados al usuario
        List<Reclamo> reclamos = reclamoRepository.findByPolizaUsuarioIdList(id);
        if (!reclamos.isEmpty()) {
            reclamoRepository.deleteAll(reclamos);
        }
        
        // Ya no se eliminan pólizas porque ahora son productos de aseguradoras
        // independientes de los usuarios
        
        // Eliminar refresh tokens asociados al usuario
        refreshTokenRepository.deleteByUsuario(u);
        
        // Eliminar el usuario
        usuarioRepository.delete(u);
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