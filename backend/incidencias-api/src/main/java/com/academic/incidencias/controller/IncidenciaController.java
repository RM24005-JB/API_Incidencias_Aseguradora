package com.academic.incidencias.controller;

import com.academic.incidencias.dto.IncidenciaDTO;
import com.academic.incidencias.service.IncidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Incidencias", description = "Operaciones CRUD para la gestión de incidencias")
@RestController
@RequestMapping("/api/v1/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @Operation(summary = "Listar todas las incidencias", description = "Obtiene una lista con todas las incidencias registradas")
    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> getAll() {
        return ResponseEntity.ok(incidenciaService.listarIncidencias());
    }

    @Operation(summary = "Obtener incidencia por ID", description = "Devuelve una incidencia específica según su ID")
    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> getById(@PathVariable @NonNull Long id) {
        Optional<IncidenciaDTO> incidencia = incidenciaService.obtenerIncidencia(id);
        return incidencia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear incidencia", description = "Registra una nueva incidencia")
    @PostMapping
    public ResponseEntity<IncidenciaDTO> create(@RequestBody IncidenciaDTO incidencia) {
        IncidenciaDTO saved = incidenciaService.crearIncidencia(incidencia);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar incidencia", description = "Modifica los datos de una incidencia existente por ID")
    @PutMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> update(@PathVariable @NonNull Long id, @RequestBody IncidenciaDTO incidencia) {
        Optional<IncidenciaDTO> updated = incidenciaService.actualizarIncidencia(id, incidencia);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar incidencia", description = "Elimina una incidencia existente por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        if (incidenciaService.eliminarIncidencia(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
