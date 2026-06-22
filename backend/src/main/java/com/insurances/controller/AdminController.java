package com.insurances.controller;

import com.insurances.dto.CambioEstadoDTO;
import com.insurances.dto.PolizaDTO;
import com.insurances.dto.ReclamoDTO;
import com.insurances.dto.RolDTO;
import com.insurances.dto.UsuarioDTO;
import com.insurances.model.EstadoReclamo;
import com.insurances.service.AdminService;
import com.insurances.service.PolizaService;
import com.insurances.service.ReclamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administracion", description = "Endpoints solo para administradores")
public class AdminController {
    private final ReclamoService reclamoService;
    private final AdminService adminService;
    private final PolizaService polizaService;

    @GetMapping("/reclamos")
    @Operation(summary = "Listar todos los reclamos (admin) con paginacion y filtros")
    public ResponseEntity<Page<ReclamoDTO>> listarTodosReclamos(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(required = false) EstadoReclamo estado,
                                                                @RequestParam(required = false) Long aseguradoraId,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(reclamoService.listarTodosConFiltros(estado, aseguradoraId, fechaDesde, fechaHasta, pageable));
    }

    @GetMapping("/polizas")
    @Operation(summary = "Listar todas las pólizas (admin) con paginacion y filtro por aseguradora")
    public ResponseEntity<Page<PolizaDTO>> listarTodasPolizas(@RequestParam(defaultValue = "0") int page,
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

    @PostMapping("/polizas")
    @Operation(summary = "Crear una póliza para un usuario específico (admin)")
    public ResponseEntity<PolizaDTO> crearPoliza(@Valid @RequestBody PolizaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(polizaService.crearComoAdmin(dto));
    }

    @GetMapping("/polizas/{id}")
    @Operation(summary = "Obtener detalle de una póliza (admin)")
    public ResponseEntity<PolizaDTO> obtenerPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtenerPorId(id));
    }

    @PutMapping("/polizas/{id}")
    @Operation(summary = "Actualizar una póliza (admin)")
    public ResponseEntity<PolizaDTO> actualizarPoliza(@PathVariable Long id, @Valid @RequestBody PolizaDTO dto) {
        return ResponseEntity.ok(polizaService.actualizarComoAdmin(id, dto));
    }

    @PutMapping("/reclamos/{id}/estado")
    @Operation(summary = "Cambiar estado de un reclamo (admin)")
    public ResponseEntity<ReclamoDTO> cambiarEstado(@PathVariable Long id,
                                                    @Valid @RequestBody CambioEstadoDTO dto,
                                                    @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(reclamoService.cambiarEstado(id, dto.getNuevoEstado(), user.getUsername()));
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar rol de un usuario")
    // FIX: Usar RolDTO existente en lugar de enum directo
    public ResponseEntity<UsuarioDTO> cambiarRol(@PathVariable Long id, @RequestBody RolDTO dto) {
        return ResponseEntity.ok(adminService.cambiarRol(id, dto.getNuevoRol()));
    }

    @PutMapping("/usuarios/{id}/toggle")
    @Operation(summary = "Activar/desactivar un usuario")
    public ResponseEntity<Void> toggleEnable(@PathVariable Long id) {
        adminService.toggleEnable(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        adminService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
}
