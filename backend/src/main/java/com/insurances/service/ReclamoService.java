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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReclamoService {
    private final ReclamoRepository reclamoRepository;
    private final PolizaRepository polizaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailSimulationService emailSimulationService;
    private final ReclamoEstadoHistorialRepository historialRepository;

    // FIX #3: @Transactional(readOnly = true) para evitar LazyInitializationException
    // al acceder a relaciones lazy en toDTO()
    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return reclamoRepository.findByUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarTodos(Pageable pageable) {
        return reclamoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarPorUsuarioConFiltros(Long usuarioId, EstadoReclamo estado, 
                                                        Long aseguradoraId, LocalDate fechaDesde,
                                                        LocalDate fechaHasta, Pageable pageable) {
        // Get all user claims first
        Page<Reclamo> result = reclamoRepository.findByUsuarioId(usuarioId, pageable);
        
        // Convert LocalDate to LocalDateTime for comparison
        LocalDateTime fechaDesdeDateTime = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : null;
        
        // Apply filters in memory for combined filters
        return result.getContent().stream()
            .filter(r -> estado == null || r.getEstado() == estado)
            .filter(r -> aseguradoraId == null || r.getPoliza().getAseguradora().getId().equals(aseguradoraId))
            .filter(r -> fechaDesdeDateTime == null || r.getFechaCreacion() == null || !r.getFechaCreacion().isBefore(fechaDesdeDateTime))
            .filter(r -> fechaHastaDateTime == null || r.getFechaCreacion() == null || !r.getFechaCreacion().isAfter(fechaHastaDateTime))
            .map(this::toDTO)
            .toList()
            .stream()
            .collect(java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
            ));
    }

    @Transactional(readOnly = true)
    public Page<ReclamoDTO> listarTodosConFiltros(EstadoReclamo estado, Long aseguradoraId,
                                                  LocalDate fechaDesde, LocalDate fechaHasta,
                                                  Pageable pageable) {
        // Get all claims first
        Page<Reclamo> result = reclamoRepository.findAll(pageable);
        
        // Convert LocalDate to LocalDateTime for comparison
        LocalDateTime fechaDesdeDateTime = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : null;
        
        // Apply filters in memory for combined filters
        return result.getContent().stream()
            .filter(r -> estado == null || r.getEstado() == estado)
            .filter(r -> aseguradoraId == null || r.getPoliza().getAseguradora().getId().equals(aseguradoraId))
            .filter(r -> fechaDesdeDateTime == null || r.getFechaCreacion() == null || !r.getFechaCreacion().isBefore(fechaDesdeDateTime))
            .filter(r -> fechaHastaDateTime == null || r.getFechaCreacion() == null || !r.getFechaCreacion().isAfter(fechaHastaDateTime))
            .map(this::toDTO)
            .toList()
            .stream()
            .collect(java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
            ));
    }

    @Transactional
    public ReclamoDTO crear(ReclamoDTO dto, Long usuarioId) {
        Poliza poliza = polizaRepository.findById(dto.getPolizaId())
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Ya no se verifica que la póliza pertenezca al usuario
        // Las pólizas son productos de aseguradoras, independientes de usuarios
        // Cualquier usuario puede hacer un reclamo sobre cualquier póliza

        Reclamo reclamo = new Reclamo();
        reclamo.setPoliza(poliza);
        reclamo.setUsuario(usuario);
        reclamo.setFechaSiniestro(dto.getFechaSiniestro());
        reclamo.setDescripcion(dto.getDescripcion());
        reclamo.setMontoEstimado(dto.getMontoEstimado());
        reclamo.setEstado(EstadoReclamo.REGISTRADO);
        reclamo.setFechaCreacion(java.time.LocalDateTime.now());
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

        if (!isAdmin && !reclamo.getUsuario().getId().equals(usuarioId)) {
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
        dto.setUsuarioEmail(r.getUsuario() != null ? r.getUsuario().getEmail() : null);
        return dto;
    }
}
