package com.academic.incidencias.service;

import com.academic.incidencias.dto.IncidenciaDTO;
import com.academic.incidencias.model.Incidencia;
import com.academic.incidencias.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    // Crear incidencia
    public IncidenciaDTO crearIncidencia(IncidenciaDTO dto) {
        Incidencia incidencia = new Incidencia();
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setEstado(dto.getEstado());

        Incidencia guardada = incidenciaRepository.save(incidencia);
        return convertirADTO(guardada);
    }

    // Listar todas las incidencias
    public List<IncidenciaDTO> listarIncidencias() {
        return incidenciaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Buscar incidencia por ID
    public Optional<IncidenciaDTO> obtenerIncidencia(Long id) {
        return incidenciaRepository.findById(id)
                .map(this::convertirADTO);
    }

    // Actualizar incidencia
    public Optional<IncidenciaDTO> actualizarIncidencia(Long id, IncidenciaDTO dto) {
        return incidenciaRepository.findById(id).map(incidencia -> {
            incidencia.setDescripcion(dto.getDescripcion());
            incidencia.setEstado(dto.getEstado());
            Incidencia actualizada = incidenciaRepository.save(incidencia);
            return convertirADTO(actualizada);
        });
    }

    // Eliminar incidencia
    public boolean eliminarIncidencia(Long id) {
        if (incidenciaRepository.existsById(id)) {
            incidenciaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Conversión Entity → DTO
    private IncidenciaDTO convertirADTO(Incidencia incidencia) {
        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setId(incidencia.getId());
        dto.setDescripcion(incidencia.getDescripcion());
        dto.setEstado(incidencia.getEstado());
        return dto;
    }
}
