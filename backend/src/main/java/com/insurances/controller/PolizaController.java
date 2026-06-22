package com.insurances.controller;

import com.insurances.dto.PolizaDTO;
import com.insurances.service.PolizaService;
import com.insurances.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas")
@RequiredArgsConstructor
@Tag(name = "Pólizas", description = "Gestión de pólizas del usuario autenticado")
public class PolizaController {
    private final PolizaService polizaService;
    private final UsuarioService usuarioService;

    private Long getUsuarioId(UserDetails user) {
        return usuarioService.getIdByEmail(user.getUsername());
    }

    @GetMapping
    @Operation(summary = "Listar todas las pólizas del sistema (con paginación y filtro por aseguradora)")
    public ResponseEntity<Page<PolizaDTO>> listarTodasPolizas(@AuthenticationPrincipal UserDetails user,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) Long aseguradoraId) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("fechaInicio").descending());
        Page<PolizaDTO> polizas;
        if (aseguradoraId != null) {
            polizas = polizaService.listarPorAseguradora(aseguradoraId, pageable);
        } else {
            polizas = polizaService.listarTodos(pageable);
        }
        return ResponseEntity.ok(polizas);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva póliza")
    public ResponseEntity<PolizaDTO> crear(@Valid @RequestBody PolizaDTO dto, @AuthenticationPrincipal UserDetails user) {
        // Ya no se necesita usuarioId para crear pólizas
        // Las pólizas son productos de aseguradoras, independientes de usuarios
        return ResponseEntity.status(HttpStatus.CREATED).body(polizaService.crearComoAdmin(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una póliza existente (solo propietario)")
    public ResponseEntity<PolizaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PolizaDTO dto, @AuthenticationPrincipal UserDetails user) {
        // Ya no se verifica usuarioId para actualizar pólizas
        // Las pólizas son productos de aseguradoras, independientes de usuarios
        return ResponseEntity.ok(polizaService.actualizarComoAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una póliza (solo propietario)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        // Ya no se verifica usuarioId para eliminar pólizas
        // Las pólizas son productos de aseguradoras, independientes de usuarios
        polizaService.eliminar(id, null);
        return ResponseEntity.noContent().build();
    }
}