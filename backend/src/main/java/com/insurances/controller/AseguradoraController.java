package com.insurances.controller;

import com.insurances.dto.AseguradoraDTO;
import com.insurances.service.AseguradoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aseguradoras")
@RequiredArgsConstructor
@Tag(name = "Aseguradoras", description = "Gestión de aseguradoras")
public class AseguradoraController {
    private final AseguradoraService aseguradoraService;

    @GetMapping
    @Operation(summary = "Listar todas las aseguradoras")
    public ResponseEntity<List<AseguradoraDTO>> listar() {
        return ResponseEntity.ok(aseguradoraService.listarTodas());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear aseguradora (solo admin)")
    public ResponseEntity<AseguradoraDTO> crear(@Valid @RequestBody AseguradoraDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aseguradoraService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar aseguradora (solo admin)")
    public ResponseEntity<AseguradoraDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AseguradoraDTO dto) {
        return ResponseEntity.ok(aseguradoraService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar aseguradora (solo admin)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        aseguradoraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}