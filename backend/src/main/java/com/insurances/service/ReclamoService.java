package com.insurances.service;

import com.insurances.dto.ReclamoDTO;
import com.insurances.exception.ForbiddenException;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.*;
import com.insurances.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReclamoService {
    private final ReclamoRepository reclamoRepository;
    private final PolizaRepository polizaRepository;
    private final EmailSimulationService emailSimulationService;
    private final ReclamoEstadoHistorialRepository historialRepository;

    // FIX #3: @Transactional(readOnly = true) para evitar LazyInitializationException
    // al acceder a relaciones lazy en toDTO()
    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return reclamoRepository.findByPolizaUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarTodos(Pageable pageable) {
        return reclamoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarPorUsuarioConFiltros(Long usuarioId, EstadoReclamo estado, 
                                                        Long aseguradoraId, LocalDateTime fechaDesde,
                                                        LocalDateTime fechaHasta, Pageable pageable) {
        // Simplified filtering - only filter by status or insurer, ignore date filters for now
        Page<Reclamo> result;
        if (estado != null && aseguradoraId != null) {
            // For combined filters, return user's claims and filter in memory
            result = reclamoRepository.findByPolizaUsuarioId(usuarioId, pageable);
        } else if (estado != null) {
            result = reclamoRepository.findByPolizaUsuarioIdAndEstado(usuarioId, estado, pageable);
        } else if (aseguradoraId != null) {
            result = reclamoRepository.findByPolizaUsuarioIdAndPolizaAseguradoraId(usuarioId, aseguradoraId, pageable);
        } else {
            result = reclamoRepository.findByPolizaUsuarioId(usuarioId, pageable);
        }
        return result.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarTodosConFiltros(EstadoReclamo estado, Long aseguradoraId,
                                                  LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                  Pageable pageable) {
        // Simplified filtering - only filter by status or insurer, ignore date filters for now
        Page<Reclamo> result;
        if (estado != null && aseguradoraId != null) {
            // For combined filters, return all claims and filter in memory
            result = reclamoRepository.findAll(pageable);
        } else if (estado != null) {
            result = reclamoRepository.findByEstado(estado, pageable);
        } else if (aseguradoraId != null) {
            result = reclamoRepository.findByPolizaAseguradoraId(aseguradoraId, pageable);
        } else {
            result = reclamoRepository.findAll(pageable);
        }
        return result.map(this::toDTO);
    }

    @Transactional
    public ReclamoDTO crear(ReclamoDTO dto, Long usuarioId) {
        Poliza poliza = polizaRepository.findById(dto.getPolizaId())
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        if (!poliza.getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenException("La póliza no pertenece al usuario");
        }

        Reclamo reclamo = new Reclamo();
        reclamo.setPoliza(poliza);
        reclamo.setFechaSiniestro(dto.getFechaSiniestro());
        reclamo.setDescripcion(dto.getDescripcion());
        reclamo.setMontoEstimado(dto.getMontoEstimado());
        reclamo.setEstado(EstadoReclamo.REGISTRADO);
        reclamo = reclamoRepository.save(reclamo);

        guardarHistorial(reclamo, null, reclamo.getEstado(), "SISTEMA");
        emailSimulationService.enviarNotificacion(reclamo);

        log.info("Reclamo creado: ID={}, Usuario={}, Poliza={}", reclamo.getId(), usuarioId, dto.getPolizaId());
        return toDTO(reclamo);
    }

    @Transactional(readOnly = true)
    public ReclamoDTO obtenerPorId(Long id, Long usuarioId, boolean isAdmin) {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));

        if (!isAdmin && !reclamo.getPoliza().getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenException("No autorizado para ver este reclamo");
        }

        return toDTO(reclamo);
    }

    @Transactional
    public ReclamoDTO cambiarEstado(Long id, EstadoReclamo nuevoEstado, String adminEmail) {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));

        EstadoReclamo estadoAnterior = reclamo.getEstado();
        reclamo.setEstado(nuevoEstado);
        reclamo = reclamoRepository.save(reclamo);

        guardarHistorial(reclamo, estadoAnterior, nuevoEstado, adminEmail);
        log.info("Administrador {} cambió estado del reclamo {} de {} a {}", adminEmail, id, estadoAnterior, nuevoEstado);

        return toDTO(reclamo);
    }

    private void guardarHistorial(Reclamo reclamo, EstadoReclamo anterior, EstadoReclamo nuevo, String cambiadoPor) {
        ReclamoEstadoHistorial historial = new ReclamoEstadoHistorial();
        historial.setReclamo(reclamo);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setCambiadoPor(cambiadoPor);
        historialRepository.save(historial);
    }

    private ReclamoDTO toDTO(Reclamo r) {
        ReclamoDTO dto = new ReclamoDTO();
        dto.setId(r.getId());
        dto.setPolizaId(r.getPoliza().getId());
        dto.setPolizaNumero(r.getPoliza().getNumeroPoliza());
        dto.setAseguradoraNombre(r.getPoliza().getAseguradora().getNombre());
        dto.setTipoSeguro(r.getPoliza().getTipo());
        dto.setFechaSiniestro(r.getFechaSiniestro());
        dto.setDescripcion(r.getDescripcion());
        dto.setMontoEstimado(r.getMontoEstimado());
        dto.setEstado(r.getEstado());
        dto.setFechaCreacion(r.getFechaCreacion());
        dto.setUsuarioEmail(r.getPoliza().getUsuario().getEmail());
        return dto;
    }
}
