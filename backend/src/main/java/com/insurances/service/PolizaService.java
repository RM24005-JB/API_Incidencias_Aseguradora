package com.insurances.service;

import com.insurances.dto.PolizaDTO;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Poliza;
import com.insurances.model.Usuario;
import com.insurances.repository.AseguradoraRepository;
import com.insurances.repository.PolizaRepository;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolizaService {
    private final PolizaRepository polizaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AseguradoraRepository aseguradoraRepository;

    public Page<PolizaDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return polizaRepository.findByUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    public Page<PolizaDTO> listarTodos(Pageable pageable) {
        return polizaRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<PolizaDTO> listarPorAseguradora(Long aseguradoraId, Pageable pageable) {
        return polizaRepository.findByAseguradoraId(aseguradoraId, pageable).map(this::toDTO);
    }

    public Page<PolizaDTO> listarPorUsuarioYAseguradora(Long usuarioId, Long aseguradoraId, Pageable pageable) {
        return polizaRepository.findByUsuarioIdAndAseguradoraId(usuarioId, aseguradoraId, pageable).map(this::toDTO);
    }

    public PolizaDTO crear(PolizaDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Poliza p = new Poliza();
        p.setUsuario(usuario);
        p.setAseguradora(aseguradoraRepository.findById(dto.getAseguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Aseguradora no encontrada")));
        p.setNumeroPoliza(dto.getNumeroPoliza());
        p.setTipo(dto.getTipo());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setCoberturas(dto.getCoberturas());
        return toDTO(polizaRepository.save(p));
    }

    public PolizaDTO actualizar(Long id, PolizaDTO dto, Long usuarioId) {
        Poliza p = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        if (!p.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes modificar una póliza que no te pertenece");
        }
        p.setAseguradora(aseguradoraRepository.findById(dto.getAseguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Aseguradora no encontrada")));
        p.setNumeroPoliza(dto.getNumeroPoliza());
        p.setTipo(dto.getTipo());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setCoberturas(dto.getCoberturas());
        return toDTO(polizaRepository.save(p));
    }

    public void eliminar(Long id, Long usuarioId) {
        Poliza p = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        if (!p.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes eliminar una póliza que no te pertenece");
        }
        polizaRepository.deleteById(id);
    }

    public PolizaDTO obtenerPorId(Long id) {
        Poliza p = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        return toDTO(p);
    }

    public PolizaDTO actualizarComoAdmin(Long id, PolizaDTO dto) {
        Poliza p = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        p.setAseguradora(aseguradoraRepository.findById(dto.getAseguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Aseguradora no encontrada")));
        p.setNumeroPoliza(dto.getNumeroPoliza());
        p.setTipo(dto.getTipo());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setCoberturas(dto.getCoberturas());
        return toDTO(polizaRepository.save(p));
    }

    private PolizaDTO toDTO(Poliza p) {
        PolizaDTO dto = new PolizaDTO();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuario().getId());
        dto.setAseguradoraId(p.getAseguradora().getId());
        dto.setNombreAseguradora(p.getAseguradora().getNombre());
        dto.setNumeroPoliza(p.getNumeroPoliza());
        dto.setTipo(p.getTipo());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFin(p.getFechaFin());
        dto.setCoberturas(p.getCoberturas());
        return dto;
    }
}